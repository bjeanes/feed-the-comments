(ns bjeanes.feed-the-comments.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[feed-the-comments starting]=-"))
   :start      (fn []
                 (log/info "\n-=[feed-the-comments started successfully]=-"))
   :stop       (fn []
                 (log/info "\n-=[feed-the-comments has shut down successfully]=-"))
   :middleware (fn [handler _] handler)
   :opts       {:profile :prod}})
