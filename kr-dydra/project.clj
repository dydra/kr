(defproject kr-dydra "0.1.0-SNAPSHOT"
  :description "A Dydra module for KR to implement knowledge bases as Dydra repositories"
  :url "http://dydra.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories [["dydrarepo" "file:///development/lib"]]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [edu.ucdenver.ccp/kr-sesame-core "1.4.17"]
                 [com.dydra/dydra-ndk "0.0.1"]])
