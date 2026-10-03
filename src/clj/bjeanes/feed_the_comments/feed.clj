(ns bjeanes.feed-the-comments.feed
  (:require [bjeanes.feed-the-comments.feed.transform :as t]
            [bjeanes.feed-the-comments.feed.rss-2]))

(def transform-feed t/transform-feed)
