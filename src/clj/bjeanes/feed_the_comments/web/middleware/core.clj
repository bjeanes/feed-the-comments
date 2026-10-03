(ns bjeanes.feed-the-comments.web.middleware.core
  (:require
   [bjeanes.feed-the-comments.env :as env]
   [bjeanes.feed-the-comments.web.routes.utils :as utils]
   [ring.middleware.defaults :as defaults]
   [ring.middleware.session.cookie :as cookie]))

(defn wrap-url-config
  "Adds the settings `utils/path-for` and `utils/url-for` need to each request."
  [handler {:keys [proxy? public-url]}]
  (let [config (merge {::utils/proxy? (boolean proxy?)}
                      (utils/parse-public-url public-url))]
    (fn
      ([request]
       (handler (merge request config)))
      ([request respond raise]
       (handler (merge request config) respond raise)))))

(defn wrap-base
  [{:keys [site-defaults-config cookie-secret public-url] :as opts}]
  (let [cookie-store (cookie/cookie-store {:key (.getBytes ^String cookie-secret)})]
    (fn [handler]
      (cond-> ((:middleware env/defaults) handler opts)
        true (wrap-url-config {:proxy?     (:proxy site-defaults-config)
                               :public-url public-url})
        true (defaults/wrap-defaults
              (assoc-in site-defaults-config [:session :store] cookie-store))))))
