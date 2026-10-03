(ns bjeanes.feed-the-comments.feed.transform
  (:require
   [clojure.data.xml :as xml]
   [clojure.string :as str]))

(xml/alias-uri 'atom "http://www.w3.org/2005/Atom")

(defn atom? [doc]
  (= :atom/feed (:tag doc)))

(defn rss-2? [doc]
  (and (= :rss (:tag doc))
       (some-> (get-in doc [:attrs :version])
               (str/starts-with? "2."))))

(defn feed-type [root]
  (cond
    (atom? root)   :feed/atom
    (rss-2? root)  :feed/rss-2
    :else          :feed/unknown))

(defmulti transform-feed feed-type)
