(ns bjeanes.feed-the-comments.web.controllers.health
  (:require
   [ring.util.http-response :as http-response]))

(defn healthcheck! [_] (http-response/ok "up"))
