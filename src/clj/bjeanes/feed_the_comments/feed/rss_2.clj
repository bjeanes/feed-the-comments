(ns bjeanes.feed-the-comments.feed.rss-2
  (:require [bjeanes.feed-the-comments.feed.transform :as t]))

(defn element? [x] (instance? clojure.data.xml.node.Element x))

(defn update-children
  "Apply `f` to all children of `element` whose tag is  named by `tag`"
  [element tag f]
  (update element :content
          (fn [children]
            (mapv
             #(if (and (element? %) (= tag (:tag %)))
                (f %)
                %)
             children))))

(defn child [element tag]
  (some #(when (and (element? %) (= tag (:tag %))) %)
        (:content element)))

(defn transform-item [item]
  (if-let [comments (child item :comments)]
    (update item :content
            (fn [children]
              (->> children
                   (keep
                    (fn [child]
                      (cond
                        (= :comments (:tag child))
                        nil
                        (= :link (:tag child))
                        (assoc child :content (:content comments))

                        :else child)))
                   vec)))
    item))

(defn transform-channel [channel]
  (update-children channel :item transform-item))

(defmethod t/transform-feed :feed/rss-2 [rss]
  (update-children rss :channel transform-channel))
