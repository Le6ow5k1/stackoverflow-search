(ns stackoverflow-search.views
  (:require [hiccup.page :refer [html5]]
            [stackoverflow-search.oauth :as oauth]
            ))

(defn home []
  (println (deref oauth/access-token))
  (html5
    [:head
     [:title "Welcome to stackoverflow-search"]]
    [:body
     [:h1 "Welcome to stackoverflow-search"]
     (if (not (nil? (deref oauth/access-token)))
       [:div "You're logged in"]
       [:div [:a {:href "/login"} "Login using StackOverflow"]]
       )
     ]))
