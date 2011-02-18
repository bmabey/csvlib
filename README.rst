================================
csvlib - CSV library for Clojure
================================

About
=====
Library to read and write CSV files with Clojure.

Usage
=====
::

    (use 'csvlib)
    ; Without headers
    (let [records (csv-read "log.csv")]
      (println ((first records) 0)))

    ; With headers
    (let [records (csv-read "log.csv" true)]
      (println ((first records) "IP")))

License
=======
Copyright (C) 2010 Miki Tebeka <miki.tebeka@gmail.com>

Distributed under the Eclipse Public License, the same as Clojure.
