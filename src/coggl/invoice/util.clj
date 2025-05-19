(ns coggl.invoice.util
  (:require [coggl.config :as conf]))

(defn invoice-css []
  (slurp (:invoice-css-path conf/env)))

(defn destination-path [f]
  (str (:cache-dir conf/env) "/" f))
