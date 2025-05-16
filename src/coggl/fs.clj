(ns coggl.fs
  (:require [babashka.fs :as fs]
            [buddy.core.codecs :as cod]))

(defn get-config-dir []
  (str
   (System/getenv "HOME")
   "/"
   ".coggle")) 

(defn has-config-dir? [path]
  (fs/directory? path))

(defn write-report! [name content]
  (babashka.fs/write-bytes name (cod/str->bytes content)))

(defn write-reports!
  ([contents options]
   (let [extension (:extension options)
         dir (or
              (:dir options)
              (get-config-dir))]
     (->> (vec (keys contents))
          (mapv (fn [client]
                  (when (not (has-config-dir? dir))
                    (fs/create-dir dir))
                  (write-report!
                   (str
                    (if (some? dir) (str dir "/") "")
                    (name client)
                    "."
                    (name (or extension :pdf)))
                   (client contents)))))))
  ([contents]
   (write-reports! contents {:extension :pdf
                            :dir (get-config-dir)})))

(comment
  (write-reports! {:turnstay "some test content"
                  :simply "some other text"} {:extension "txt" :dir (get-config-dir)})
  )

(defn has-previous-session? [path]
  (fs/regular-file? path))

(defn write-session-file [path content]
  (when-not (has-previous-session? path)
    (fs/create-file path))
  (fs/update-file path (fn [_] content)))

(defn read-session-file [path]
  (-> path
   (fs/read-all-lines)
   (clojure.string/join)))


