(ns recipe-builder.app
  (:require [recipe-builder.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
