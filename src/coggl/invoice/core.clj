(ns coggl.invoice.core
  (:require [clj-htmltopdf.core :refer :all]
            [hiccup.page :as h]
            [coggl.invoice.components :as invoice]
            [coggl.invoice.util :as util]))

(defn render [app]
  (h/html5 app))

(defn generate-invoice [invoice-data]
  (-> (invoice/invoice invoice-data)
      (render)
      (->pdf (util/destination-path (str (:invoice-number {:invoice-number "test"}) ".pdf")))))

