(ns stackoverflow-search.handler
  (:require [compojure.core :refer [defroutes routes wrap-routes GET]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]
            [clj-http.client :as http]
            [clojure.core.async :as async]
            [environ.core :refer [env]]
            [cheshire.core :as json]))

(def max-http-connections (-> (env :max-http-connections)
                              Integer/parseInt))
(def search-api-url "https://api.stackexchange.com/2.2/search")
(def search-api-params {:pagesize 100
                        :order "desc"
                        :sort "creation"
                        :site "stackoverflow"
                        :key (env :api-key)})

(defn search-for-tag
  [tag]
  (let [response-channel (async/chan)
        query-params (assoc search-api-params :tagged tag)]
    (http/get search-api-url
                {:async? true :query-params query-params :as :json}
                (fn [response] (async/>!! response-channel response))
                (fn [exception] (async/>!! response-channel exception)))
    response-channel))

(defn fetch-answers
  [response]
  (let [answers (get-in response [:body :items])]
    (map #(select-keys % [:is_answered :tags]) answers)))

(defn search-for-tags
  [tags]
  (http/with-async-connection-pool {:threads max-http-connections}
    (->> tags
         (map search-for-tag)
         doall
         (map (comp fetch-answers async/<!!)))))

(defroutes api-routes
  (GET "/search" [tag] (response (search-for-tags tag))))

(defroutes site-routes
  )

(def app
  (routes
   (-> api-routes
       (wrap-routes wrap-json-response)
       (wrap-defaults api-defaults))
   (-> site-routes
       (wrap-defaults site-defaults))))
