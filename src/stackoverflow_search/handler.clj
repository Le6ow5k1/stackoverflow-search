(ns stackoverflow-search.handler
  (:require [compojure.core :refer [defroutes routes wrap-routes GET]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]
            [clj-http.client :as http]
            [clojure.core.async :as async]
            [environ.core :refer [env]]))

(def proxy-host (env :proxy-host))
(def proxy-port (env :proxy-port))
(def max-http-connections (-> (env :max-http-connections) Integer/parseInt))
(def default-http-options (let [general-options {:async? true :as :json}]
                            (if (and proxy-host proxy-port)
                              (merge general-options {:proxy-host proxy-host :proxy-port proxy-port})
                              general-options)))
(def search-api-url "https://api.stackexchange.com/2.2/search")
(def search-api-params {:pagesize 100
                        :order "desc"
                        :sort "creation"
                        :site "stackoverflow"
                        :key (env :api-key)})

(defn set-or-update-value
  [m key initial-value update-value-fn]
  (if (nil? (get-in m key))
    (assoc-in m key initial-value)
    (update-in m key update-value-fn)))

(defn aggregate-question-tags
  [tags-stats {answered? :is_answered tags :tags}]
  (reduce
   (fn [tags-stats tag]
     (let [stats-with-total (set-or-update-value tags-stats [tag :total] 1 inc)]
       (if answered?
         (set-or-update-value stats-with-total [tag :answered] 1 inc)
         (set-or-update-value stats-with-total [tag :answered] 0 identity))))
   tags-stats
   tags))

(defn aggregate-tags
  [questions]
  (reduce aggregate-question-tags {} questions))

(defn fetch-questions
  [response]
  (let [questions (get-in response [:body :items])]
    (map #(select-keys % [:is_answered :tags]) questions)))

(defn search-by-tag
  [tag]
  (let [response-channel (async/chan)
        query-params (assoc search-api-params :tagged tag)]
    (http/get search-api-url
              (assoc default-http-options :query-params query-params)
              (fn [response] (async/>!! response-channel response))
              (fn [exception] (async/>!! response-channel exception)))
    response-channel))

(defn search-by-tags
  [tags]
  (http/with-async-connection-pool {:threads max-http-connections}
    (->> tags
         (map search-by-tag)
         doall
         (mapcat (comp fetch-questions async/<!!))
         aggregate-tags)))

(defroutes api-routes
  (GET "/search" [tag] (response (search-by-tags tag))))

(def app
  (routes
   (-> api-routes
       (wrap-routes wrap-json-response)
       (wrap-defaults api-defaults))))
