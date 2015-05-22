(ns graphite.render-test
  (:require [clojure.test :refer :all]
            [graphite.render :refer :all])
  (:import (java.time LocalDateTime)))


(deftest url-builder
  (testing "simple target"
    (is (= "http://localhost/render?target=series"
           (build-url "http://localhost"
                      "series"))))

  (testing "function target"
    (is (= "http://localhost/render?target=summarize%28series%2C%221hour%22%2C%22sum%22%29"
           (build-url "http://localhost"
                      (func "series", :summarize, "1hour", "sum")))))

  (testing "function target with no params"
    (is (= "http://localhost/render?target=absolute%28series%29"
           (build-url "http://localhost"
                      (func "series", :absolute)))))

  (testing "nested function target"
    (is (= "http://localhost/render?target=removeAboveValue%28summarize%28series%2C%221hour%22%2C%22sum%22%29%2C10%29"
           (build-url "http://localhost"
                      (-> "series"
                          (func :summarize "1hour" "sum")
                          (func :removeAboveValue 10))))))

  (testing "string render options"
    (is (= "http://localhost/render?target=series&format=json"
           (build-url "http://localhost"
                      "series"
                      {:format "json"}))))

  (testing "date render options"
    (is (= "http://localhost/render?target=series&from=03%3A04_20150102"
           (build-url "http://localhost"
                      "series"
                      {:from (LocalDateTime/of 2015 1 2 3 4)}))))
  )
