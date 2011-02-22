(ns csvlib-test
  (:use [csvlib] :reload-all)
  (:use [clojure.test]))

(deftest test-no-headers
  (let [records (read-csv "test/toons.csv")]
    (is (= (count records) 4))
    (is (= (first records) {0 "Name" 1 "Animal" 2 "Funny"}))
    (is (= (last records) { 0 "Elmer" 1 "Pig" 2 "No"}))))

(deftest test-headers
  (let [records (read-csv "test/toons.csv" :headers? true)]
    (is (= (count records) 3))
    (is (= (first records) {"Name" "Duffy" "Animal" "Duck" "Funny" "Yes"}))
    (is (= (last records) { "Name" "Elmer" "Animal" "Pig" "Funny" "No"}))))

(deftest test-convert
  (let [conv {"Funny" {"Yes" true "No" false}}
        records (read-csv "test/toons.csv" :headers? true :convert conv)]
    (is (= (count records) 3))
    (is (= ((first records) "Funny") true))))
