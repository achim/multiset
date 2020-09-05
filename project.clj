(defproject org.clojars.achim/multiset "0.1.1-SNAPSHOT"

  :description "A simple multiset/bag implementation for Clojure."
  :url "https://github.com/achim/multiset"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :repositories {"clojars-https" {:url "https://clojars.org/repo"
                                  :username "achim"
                                  :password :env}}

  :source-paths ["src" "src/main"]
  :test-paths ["test" "src/test"]

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/algo.generic "0.1.3"]]

  :profiles {:dev {:dependencies [[midje "1.9.9"]]
                   :plugins [[lein-html5-docs "3.0.1"]
                             [lein-midje "3.1.3"]]
                   :html5-docs-docs-dir "doc"
                   :html5-docs-ns-includes #"multiset\..*"
                   :html5-docs-ns-excludes #".*\.t_.*"
                   :html5-docs-repository-url ""}})
