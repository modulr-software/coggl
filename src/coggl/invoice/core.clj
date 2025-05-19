(ns coggl.invoice.core
  (:require [clj-htmltopdf.core :refer [->pdf]]
            [hiccup.page :as h]
            [coggl.invoice.components :as invoice]
            [coggl.invoice.util :as util]))

(defn render [app]
  (h/html5 app))

(defn generate-invoice [{:keys [invoice-number] :as invoice-data}]
  (-> (invoice/invoice invoice-data)
      (render)
      (->pdf (util/destination-path (str invoice-number ".pdf")))))

