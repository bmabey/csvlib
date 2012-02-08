(defproject org.clojars.bmabey/csvlib "0.3.2"
  :description "A CSV library for Clojure, using csvreader.com"
  :url "https://github.com/bmabey/csvlib"
  :warn-on-reflection true
  :multi-deps {"1.3" [[org.clojure/clojure "1.3.0"]]
               "1.2" [[org.clojure/clojure "1.2.1"]]
               :all [[net.sourceforge.javacsv/javacsv "2.0"]]}
  :dependencies [[net.sourceforge.javacsv/javacsv "2.0"]])
