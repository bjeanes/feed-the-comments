(ns bjeanes.feed-the-comments.web.htmx
  (:require
   [ring.util.http-response :as http-response]
   [hiccup2.core :as h]))

(defmacro page [opts & content]
  `(-> (str (h/html ~opts
                    (h/raw "<!DOCTYPE html>\n")
                    [:html {:lang "en"}
                     [:head [:meta {:charset "utf-8"}]]
                     [:body ~@content]]))
       http-response/ok
       (http-response/content-type "text/html")))

(defmacro fragment [opts & content]
  `(-> (str (h/html ~opts ~@content))
       http-response/ok
       (http-response/content-type "text/html")))
