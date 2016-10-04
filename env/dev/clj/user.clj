(ns user
  (:require [mount.core :as mount]
            [recipe-builder.figwheel :refer [start-fw stop-fw cljs]]
            recipe-builder.core))

(defn start []
  (mount/start-without #'recipe-builder.core/repl-server))

(defn stop []
  (mount/stop-except #'recipe-builder.core/repl-server))

(defn restart []
  (stop)
  (start))


