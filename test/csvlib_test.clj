(ns csvlib-test
  (:import java.io.File)
  (:use [csvlib] :reload-all)
  (:use [clojure.test])
  (:require clojure.string))

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

(deftest test-convert-fields
  (let [conv {"Funny" {"Yes" true "No" false}}
        records (read-csv "test/toons.csv" :headers? true :convert conv)]
    (is (= (count records) 3))
    (is (= ((first records) "Funny") true))))

(deftest test-convert-headers
  (let [records (read-csv "test/toons.csv" :headers? true :convert-headers keyword)]
    (is (= (-> records first keys) [:Name :Animal :Funny]))))

(defn tempfile []
  (let [file (File/createTempFile "-csvlib-" nil)]
    (.deleteOnExit file)
    (.getCanonicalPath file)))

(defmacro defwriter-test [test-name & body]
  `(deftest ~test-name
     (let [~'tmp (tempfile)]
       ~@body)))

(defwriter-test test-write-simple
  (write-csv [[1 2 3] [4 5 6]] tmp)
  (is (= (slurp tmp) "1,2,3\n4,5,6\n")))

(defwriter-test test-write-headers
  (write-csv [[1 2 3] [4 5 6]] tmp :headers ["x" "y" "z"])
  (is (= (slurp tmp) "x,y,z\n1,2,3\n4,5,6\n")))

(defwriter-test test-write-headers-as-list
  (write-csv [[1 2 3] [4 5 6]] tmp :headers (map clojure.string/upper-case ["x" "y" "z"]))
  (is (= (slurp tmp) "X,Y,Z\n1,2,3\n4,5,6\n")))


(defwriter-test test-write-headers-and-lists
  (write-csv [(list 1 2 3) (list 4 5 6)] tmp :headers ["x" "y" "z"])
  (is (= (slurp tmp) "x,y,z\n1,2,3\n4,5,6\n")))

(defwriter-test test-write-format
  (let [format {0 #(format "format-%s" %)}]
    (write-csv [[1 2 3] [4 5 6]] tmp :format format)
    (is (= (slurp tmp) "format-1,2,3\nformat-4,5,6\n"))))

(defwriter-test test-write-mapseq
  (write-csv [{"x" 1 "y" 2} {"x" 3 "y" 4}] tmp)
  (is (= (slurp tmp) "x,y\n1,2\n3,4\n")))

(defwriter-test test-write-mapseq-with-headers
  (write-csv [{"x" 1 "y" 2} {"x" 3 "y" 4}] tmp)
  (is (= (slurp tmp) "x,y\n1,2\n3,4\n")))

(defwriter-test test-with-csv-writer
  (with-csv-writer tmp {:headers ["x" "y" "z"]} [write-csv]
    (write-csv [1 2 3])
    (write-csv [4 5 6]))
  (is (= (slurp tmp) "x,y,z\n1,2,3\n4,5,6\n")))

(defwriter-test test-with-csv-writer-appending
  (with-csv-writer tmp {:headers ["x" "y" "z"]} [write-csv]
    (write-csv [1 2 3]))
  (with-csv-writer tmp {:headers ["x" "y" "z"] :append true} [write-csv]
    (write-csv [4 5 6]))
  (is (= (slurp tmp) "x,y,z\n1,2,3\n4,5,6\n")))
