(ns recipe-builder.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[recipe-builder started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[recipe-builder has shut down successfully]=-"))
   :middleware identity})
