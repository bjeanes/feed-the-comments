(ns bjeanes.feed-the-comments.b64u
  [:import [java.util Base64]])

(defn encode [^String text]
  (let [bytes (.getBytes text "UTF-8")
        encoder (-> (Base64/getUrlEncoder)
                    (.withoutPadding))]
    (.encodeToString encoder bytes)))

(defn decode [^String encoded-text]
  (let [decoder (Base64/getUrlDecoder)
        decoded-bytes (.decode decoder encoded-text)]
    (String. decoded-bytes "UTF-8")))
