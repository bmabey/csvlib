(ns csvlib-test
  (:import java.io.File)
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

(defn tempfile []
  (.getCanonicalPath (File/createTempFile "-csvlib-" nil)))

(deftest test-write-simple
  (let [tmp (tempfile)]
    (write-csv [[1 2 3] [4 5 6]] tmp)
    (is (= (slurp tmp) "1,2,3\n4,5,6\n"))))

(deftest test-write-headers
  (let [tmp (tempfile)]
    (write-csv [[1 2 3] [4 5 6]] tmp :headers ["x" "y" "z"])
    (is (= (slurp tmp) "x,y,z\n1,2,3\n4,5,6\n"))))

(deftest test-write-format
  (let [tmp (tempfile)
        format {0 #(format "format %s" %)}]
    (write-csv [[1 2 3] [4 5 6]] tmp format)
    (is (= (slurp tmp) "format-1,2,3\nformat-4,5,6\n"))))

