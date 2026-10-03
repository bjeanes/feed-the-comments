(ns bjeanes.feed-the-comments.web.routes.utils
  (:require
   [reitit.core :as r]))

(def route-data-path [:reitit.core/match :data])

(defn route-data
  [req]
  (get-in req route-data-path))

(defn route-data-key
  [req k]
  (get-in req (conj route-data-path k)))

(defn origin [{:keys [scheme server-name server-port]}]
  (str (name scheme)
       "://"
       server-name
       (when-not (or (and (= scheme :http) (= server-port 80))
                     (and (= scheme :https) (= server-port 443)))
         (str ":" server-port))))

(defn path-for
  ([request route-name] (path-for request route-name nil))
  ([request route-name params]
   (let [router (::r/router request)]
     (-> router
         (r/match-by-name route-name params)
         r/match->path))))

(defn url-for
  ([request route-name] (url-for request route-name nil))
  ([request route-name params]
   (let [origin (origin request)
         path (path-for request route-name params)]
     (str origin path))))
