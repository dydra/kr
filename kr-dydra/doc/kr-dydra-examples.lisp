(ns com.dydra.kr.sesame.kb.examples
  (use edu.ucdenver.ccp.kr.kb
       ; edu.ucdenver.ccp.kr.rdf
       ; edu.ucdenver.ccp.kr.sparql
       ; edu.ucdenver.ccp.kr.sesame.rdf
       com.dydra.kr.sesame.kb
       )
  )

;;; (in-ns 'com.dydra.kr.sesame.kb)
;;;
;;; prepare the shell environment with the location for the dydra ndk shared library
;;; and that of the leinigen executable
;;;
;;; export LD_LIBRARY_PATH=/opt/dydra/lib:$LD_LIBRARY_PATH
;;; export PATH=/development/bin:$PATH
;;;
;;; make the dydra ndk jni component available in a local library, for example, in /development/lib
;;;
;;; $ lein deploy dydrarepo com.dydra/dydra-ndk "0.0.1" /opt/dydra/share/java/dydra-ndk.jar
;;; 
;;; reflect that location in the project.clj declaration
;;;
;;; :repositories [["dydrarepo" "file:///development/lib"]]
;;; :dependencies [ ... [com.dydra/dydra-ndk "0.0.1"] ]
;;;
;;; then start the interactive environment
;;;
;;; $ cd kr/kr-dydra/
;;; kr-dydra$ lein repl
;;;
;;; if the librqry is not found, double-check the class path
;;;
;;; (sort (map (memfn getPath) (seq (.getURLs (java.lang.ClassLoader/getSystemClassLoader)))))
;;;
;;; be careful with the namespace declaration
;;; CompilerException java.lang.Exception: Nothing specified to load, compiling:(/private/var/folders/4n/vh45gkx14rlgzz0mjn3322880000gn/T/form-init7677724696892656687.clj:1:1) 
;;; means that the 'ns form had an empty use clause

;;; the direct ndk api implementes concrete elementary sesame classes

(import com.dydra.ndk.sesame.DydraRepositoryManager)
(import com.dydra.ndk.sesame.DydraRepository)
(def manager (DydraRepositoryManager. ))
(def repository (.getRepository manager "bendiken/test"))
;;; the repository is also available directly as a concrete class
;;; (def repository (com.dydra.ndk.sesame.DydraRepository. "bendiken/test"))
;;; the interface is apparent through introspection
;;; (-> com.dydra.ndk.sesame.DydraRepository .getClass .getDeclaredMethods pprint)
(.initialize repository)
(def connection (.getConnection repository))
(def query (.prepareTupleQuery connection org.openrdf.query.QueryLanguage/SPARQL "SELECT * WHERE {?s ?p ?o}"))
(def results (.evaluate query))

(loop []
  (if (.hasNext results)
      (do (println (.next results)) (recur))))
;;; if this yields no result, double-check the repository designator

(.close results)
(.shutDown repository)  ; can take a while



;;; the kb interface

(in-ns 'com.dydra.kr.sesame.kb.examples)

(def server (new-dydra-server :repo-name "bendiken/test"))

(edu.ucdenver.ccp.kr.sesame.sparql/sesame-ask-sparql server "ask where {?s ?p ?o}")
(edu.ucdenver.ccp.kr.sparql/ask-sparql server "ask where {?s ?p ?o}")

(edu.ucdenver.ccp.kr.sesame.sparql/sesame-query-sparql server "select (count(*) as ?count) where {?s ?p ?o}")
(edu.ucdenver.ccp.kr.sparql/query-sparql server "select (count(*) as ?count) where {?s ?p ?o}")

(edu.ucdenver.ccp.kr.sesame.sparql/sesame-count-sparql server "select * where {?s ?p ?o}")
(edu.ucdenver.ccp.kr.sparql/count-sparql server "select * where {?s ?p ?o}")

(edu.ucdenver.ccp.kr.sesame.sparql/sesame-visit-sparql server println "select * where {?s ?p ?o}")
(edu.ucdenver.ccp.kr.sparql/visit-sparql server println "select * where {?s ?p ?o}")

(edu.ucdenver.ccp.kr.sesame.sparql/sesame-construct-sparql server "construct {?s ?p ?o} where {?s ?p ?o}")
(edu.ucdenver.ccp.kr.sparql/construct-sparql server "construct {?s ?p ?o} where {?s ?p ?o}")

(edu.ucdenver.ccp.kr.sesame.sparql/sesame-construct-visit-sparql server println "construct {?s ?p ?o} where {?s ?p ?o}")
(edu.ucdenver.ccp.kr.sparql/construct-visit-sparql server println "construct {?s ?p ?o} where {?s ?p ?o}")

(edu.ucdenver.ccp.kr.rdf/register-ns server "rdf" "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
(edu.ucdenver.ccp.kr.rdf/register-ns server "rdfs" "http://www.w3.org/2000/01/rdf-schema#")
(edu.ucdenver.ccp.kr.rdf/register-ns server "xsd" "http://www.w3.org/2001/XMLSchema#")


(edu.ucdenver.ccp.kr.rdf/resource-ify-uri server 'rdf/type )

(def server
     (edu.ucdenver.ccp.kr.rdf/update-ns-mapping server "rdf" "http://www.w3.org/1999/02/22-rdf-syntax-ns#"))
(edu.ucdenver.ccp.kr.sesame.rdf/sesame-query-statement server nil 'rdf/type nil nil)
(edu.ucdenver.ccp.kr.rdf/query-statement server nil 'rdf/type nil nil)
(edu.ucdenver.ccp.kr.rdf/ask-statement server nil 'rdf/type nil nil)

