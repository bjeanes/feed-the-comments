(ns bjeanes.feed-the-comments.env
  (:require
    [clojure.tools.logging :as log]
    [bjeanes.feed-the-comments.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[feed-the-comments starting using the development or test profile]=-"))
   :start      (fn []
                 (log/info "\n-=[feed-the-comments started successfully using the development or test profile]=-"))
   :stop       (fn []
                 (log/info "\n-=[feed-the-comments has shut down successfully]=-"))
   :middleware wrap-dev
   :opts       {:profile       :dev}})
