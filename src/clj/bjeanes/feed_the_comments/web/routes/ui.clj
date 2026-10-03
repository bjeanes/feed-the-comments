(ns bjeanes.feed-the-comments.web.routes.ui
  (:require
   [bjeanes.feed-the-comments.b64u :as b64u]
   [bjeanes.feed-the-comments.web.htmx :refer [fragment page] :as htmx]
   [bjeanes.feed-the-comments.web.middleware.exception :as exception]
   [bjeanes.feed-the-comments.web.routes.api :as api]
   [bjeanes.feed-the-comments.web.routes.utils :refer [path-for url-for]]
   [integrant.core :as ig]
   [reitit.ring.middleware.parameters :as parameters]
   [ring.util.http-response :as http-response]))

(defn home [request]
  (page {:lang "en"}
        [:head
         [:meta {:charset "UTF-8"}]
         [:title "Feed The Comments"]
         [:script {:src "https://unpkg.com/htmx.org@4.0.0/dist/htmx.min.js" :defer true}]]
        [:body
         [:h1 "Feed The Comments"]
         [:div "Proxy an RSS feed to promote the " [:code "<comments>"] " to the main " [:code "<link>"]
          ", for clients which don't have an option to link to the comments instead of the main article."]
         [:input {:hx-query (path-for request ::feed-url)
                  :hx-target "#result"
                  :hx-trigger "load, input changed delay:250ms"
                  :name "feed-url"
                  :placeholder "Feed URL here"}]
         [:div#result]]))

(defn feed-url [request]
  (let [upstream-url (-> request :params :feed-url)]
    (if-not (empty? upstream-url)
      (let [encoded-feed-url (b64u/encode upstream-url)
            proxied-feed-url (url-for request ::api/feed {:encoded-feed-url encoded-feed-url})]
        (fragment [:pre proxied-feed-url]
                  [:a {:href proxied-feed-url} "View!"]))
      (http-response/unprocessable-entity))))

;; Routes
(defn ui-routes [_opts]
  [["/" {:get #'home}]
   ["/feed-url" {:name ::feed-url
                 :query #'feed-url}]])

(def route-data
  {:middleware
   [;; Default middleware for ui
    ;; query-params & form-params
    parameters/parameters-middleware
    ;; exception handling
    exception/wrap-exception]})

(derive :reitit.routes/ui :reitit/routes)

(defmethod ig/init-key :reitit.routes/ui
  [_ {:keys [base-path]
      :or   {base-path ""}
      :as   opts}]
  (fn [] [base-path route-data (ui-routes opts)]))
