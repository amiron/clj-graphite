(ns graphite.render
  (:require [aleph.http :as http]
            [manifold.deferred :as d]
            [byte-streams :as bs]
            [clojure.data.json :as json]
            [clojure.string :as string])
  (:import (java.net URLEncoder)
           (java.time.format DateTimeFormatter)))

(defn- prn-param [param]
  (cond
    (string? param) (str "\"" param "\"")
    :default param))

(def date-formatter (DateTimeFormatter/ofPattern "HH:mm_YYYYMMdd"))

(defn- prn-opt [opt]
  (cond
    (instance? java.time.LocalDateTime opt) (.format date-formatter opt)
    :default opt))

(defn- build-target [call]
  (if (string? call)
    call
    (let [{:keys [func target params]} call]
      (str (name func) "("
           (build-target target)
           (when (seq params)
             (str "," (string/join "," (map prn-param params))))
           ")"))))

(defn build-url [service-url call & [opts]]
  (str service-url
       "/render?target="
       (URLEncoder/encode (build-target call))
       (when (seq opts)
         (str "&"
              (string/join "&" (map (fn [[k v]]
                                      (str (name k) "=" (URLEncoder/encode (prn-opt v))))
                                    opts))))))

(defn func [target func & params]
  {:func   func
   :target target
   :params params})

(defn render [service-url call & [opts]]
  (d/chain (http/get (build-url service-url call (assoc opts :format "json")))
           :body
           bs/to-string
           json/read-str
           #(get (first %) "datapoints")))
