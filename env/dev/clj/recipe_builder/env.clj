(ns recipe-builder.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [recipe-builder.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[recipe-builder started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[recipe-builder has shut down successfully]=-"))
   :middleware wrap-dev})
