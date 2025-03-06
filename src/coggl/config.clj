(ns coggl.config
  (:require [aero.core :refer [read-config]]
            [babashka.fs :as fs]))

(def env (read-config (fs/file "config.edn")))
