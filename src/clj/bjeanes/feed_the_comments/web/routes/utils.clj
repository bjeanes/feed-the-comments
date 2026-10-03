(ns bjeanes.feed-the-comments.web.routes.utils
  (:require
   [clojure.string :as str]
   [reitit.core :as r]))

(def route-data-path [:reitit.core/match :data])

(defn route-data
  [req]
  (get-in req route-data-path))

(defn route-data-key
  [req k]
  (get-in req (conj route-data-path k)))

(defn- first-forwarded
  "The first value of a (possibly comma-separated) forwarded header, or nil."
  [{:keys [headers] ::keys [proxy?]} header]
  (when proxy?
    (some-> (get headers header)
            (str/split #",")
            first
            str/trim
            not-empty)))

(defn trim-path
  "Normalises a path prefix to have a leading slash and no trailing slash, so
  \"/\" and \"\" both become \"\"."
  [path]
  (let [path (str/replace path #"/+$" "")]
    (if (or (str/blank? path) (str/starts-with? path "/"))
      path
      (str "/" path))))

(defn parse-public-url
  "Splits a `PUBLIC_URL` value into the parts URL building uses. A full URL
  fixes both the origin and the path prefix; a bare path fixes only the prefix.
  Throws if the value is neither."
  [public-url]
  (when-not (str/blank? public-url)
    (let [uri (java.net.URI. public-url)]
      (cond
        (and (#{"http" "https"} (.getScheme uri)) (.getHost uri))
        {::public-origin (str (.getScheme uri) "://" (.getRawAuthority uri))
         ::public-prefix (trim-path (or (.getRawPath uri) ""))}

        (and (nil? (.getScheme uri)) (str/starts-with? public-url "/"))
        {::public-prefix (trim-path (.getRawPath uri))}

        :else
        (throw (ex-info "PUBLIC_URL must be an absolute http(s) URL or a path starting with /"
                        {:public-url public-url}))))))

(defn origin
  "Build the origin to use in URL building. If `::public-origin` is set (when
  PUBLIC_URL env var is set and is an absolute URL), returns this. Otherwise,
  uses `x-forwarded-host` header from request."
  [{:keys [scheme server-name server-port] ::keys [public-origin] :as request}]
  (or public-origin
      (str (name scheme)
           "://"
           (or (first-forwarded request "x-forwarded-host")
               (str server-name
                    (when-not (or (and (= scheme :http) (= server-port 80))
                                  (and (= scheme :https) (= server-port 443)))
                      (str ":" server-port)))))))

(defn path-prefix
  "The prefix the outside world sees in front of this app's routes: from
  `PUBLIC_URL` if set, otherwise `X-Forwarded-Prefix` (when proxy headers are
  trusted). Only affects generated paths, never route matching."
  [{::keys [public-prefix] :as request}]
  (or public-prefix
      (some-> (first-forwarded request "x-forwarded-prefix") trim-path)
      ""))

(defn path-for
  ([request route-name] (path-for request route-name nil))
  ([request route-name params]
   (let [router (::r/router request)]
     (some->> (r/match-by-name router route-name params)
              r/match->path
              (str (path-prefix request))))))

(defn url-for
  ([request route-name] (url-for request route-name nil))
  ([request route-name params]
   (let [origin (origin request)
         path (path-for request route-name params)]
     (str origin path))))
