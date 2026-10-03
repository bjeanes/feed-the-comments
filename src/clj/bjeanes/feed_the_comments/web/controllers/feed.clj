(ns bjeanes.feed-the-comments.web.controllers.feed
  (:require
   [bjeanes.feed-the-comments.b64u :as b64u]
   [bjeanes.feed-the-comments.feed :refer [transform-feed]]
   [clj-http.client :as http]
   [clojure.data.xml :as xml]))

;; TODO:
;; - proper Atom support
;; - caching rewritten feed, with regard to Cache-Control
;; - SSRF protection
;;     - custom redirect strategy and/or
;;     - custom DNS resolution (:dns-resolver option) to fail any request that returns an unsafe A/AAAA record

(defn fetch-feed
  [feed-url]
  (let [{:keys [body status headers]} (http/get feed-url
                                                {:as :stream :redirect-strategy :none})
        xml (transform-feed (xml/parse body))]
    {:status status
     :body (xml/emit-str xml)
     :headers (select-keys headers ["cache-control"
                                    "content-type"
                                    "etag" ; perhaps generating own etag is preferable...
                                    "last-modified"])}))

(defn proxy-feed
  [{:keys [path-params]}]
  (let [encoded-feed-url (:encoded-feed-url path-params)
        feed-url (b64u/decode encoded-feed-url)
        feed (fetch-feed feed-url)]
    feed))
