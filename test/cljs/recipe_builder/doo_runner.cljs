(ns recipe-builder.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [recipe-builder.core-test]))

(doo-tests 'recipe-builder.core-test)

