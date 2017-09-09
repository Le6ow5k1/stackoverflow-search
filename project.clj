(defproject stackoverflow-search "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.6.0"]
                 [hiccup "1.0.5"]
                 [ring-server "0.5.0"]
                 [ring/ring-defaults "0.3.1"]
                 [ring/ring-json "0.4.0"]
                 [clj-http "3.7.0"]
                 [org.clojure/core.async "0.3.443"]
                 [environ "1.1.0"]
                 [cheshire "5.8.0"]]
  :plugins [[lein-ring "0.12.1"]
            [lein-environ "1.1.0"]]
  :ring {:handler stackoverflow-search.handler/app}
  :profiles
  {:uberjar {:aot :all}
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}}
   :dev
   {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.5.1"]]}})
