(ns com.dydra.kr.sesame.kb
  (use edu.ucdenver.ccp.kr.kb
       edu.ucdenver.ccp.kr.rdf
       edu.ucdenver.ccp.kr.sparql
       edu.ucdenver.ccp.kr.sesame.rdf
       edu.ucdenver.ccp.kr.sesame.sparql
       edu.ucdenver.ccp.kr.sesame.kb
       com.dydra.kr.sesame.rdf
       )
  (import
   ;;edu.ucdenver.ccp.kr.sesame.rdf

   org.openrdf.model.Statement

   org.openrdf.model.impl.StatementImpl
   org.openrdf.model.impl.URIImpl

   org.openrdf.repository.Repository
   org.openrdf.repository.http.HTTPRepository
   org.openrdf.repository.RepositoryConnection

   org.openrdf.repository.sail.SailRepository;
   org.openrdf.sail.memory.MemoryStore;

   org.openrdf.query.resultio.TupleQueryResultFormat

   com.dydra.ndk.sesame.DydraRepositoryManager
   com.dydra.ndk.sesame.DydraRepository
))

;;; --------------------------------------------------------
;;; specials
;;; --------------------------------------------------------


;;; --------------------------------------------------------
;;; triple store connection and setup
;;; --------------------------------------------------------



;;; --------------------------------------------------------
;;; connections
;;; --------------------------------------------------------

(declare dydra-initialize
         ensure-dydra-connection
         close-existing-dydra-connection)

(defn dydra-connection [kb]
  (ensure-dydra-connection kb))

(defn close-dydra-connection [kb]
  (close-existing-dydra-connection kb))


;;; --------------------------------------------------------
;;; namespaces
;;; --------------------------------------------------------

;;; --------------------------------------------------------
;;; protocol implementation
;;; --------------------------------------------------------

(defrecord DydraKB [repository connection kb-features]
  KB

  (native [kb] repository)
  (initialize [kb] (dydra-initialize kb))
  (open [kb] (ensure-dydra-connection kb))
  (close [kb] (close-dydra-connection kb))
  (features [kb] kb-features)

  rdfKB

  (root-ns-map [kb](sesame-server-ns-map kb))
  (register-ns [kb short long]  (sesame-register-ns kb short long))

  ;; term constructors
  (create-resource [kb name] (sesame-create-resource kb name))
  (create-property [kb name] (sesame-create-property kb name))
  (create-literal [kb val] (sesame-create-literal kb val))
  (create-literal [kb val type] (sesame-create-literal kb val type))

  (create-string-literal [kb str] (sesame-create-string-literal kb str))
  (create-string-literal [kb str lang]
                         (sesame-create-language-string-literal kb str lang))


  (create-blank-node [kb name] (sesame-create-blank-node kb name))
  (create-statement [kb s p o] (sesame-create-statement kb s p o))

  (add-statement [kb stmt] (sesame-add-statement kb stmt))
  (add-statement [kb stmt context] (sesame-add-statement kb stmt context))
  (add-statement [kb s p o] (sesame-add-statement kb s p o))
  (add-statement [kb s p o context] (sesame-add-statement kb s p o context))

  (add-statements [kb stmts] (sesame-add-statements kb stmts))
  (add-statements [kb stmts context] (sesame-add-statements kb stmts context))

  (ask-statement  [kb s p o context] (sesame-ask-statement kb s p o context))
  (query-statement [kb s p o context] (sesame-query-statement kb s p o context))


  (load-rdf-file [kb file] (sesame-load-rdf-file kb file))
  (load-rdf-file [kb file type] (sesame-load-rdf-file kb file type))

  ;;the following will throw exception for unknown rdf format
  (load-rdf-stream [kb stream] (sesame-load-rdf-stream kb stream))

  (load-rdf-stream [kb stream type] (sesame-load-rdf-stream kb stream type))



  sparqlKB

  (ask-pattern [kb pattern]
             (sesame-ask-pattern kb pattern))
  (ask-pattern [kb pattern options]
             (sesame-ask-pattern kb pattern options))

  (query-pattern [kb pattern]
        (sesame-query-pattern kb pattern))
  (query-pattern [kb pattern options]
        (sesame-query-pattern kb pattern options))

  (count-pattern [kb pattern]
        (sesame-count-pattern kb pattern))
  (count-pattern [kb pattern options]
        (sesame-count-pattern kb pattern options))

  (visit-pattern [kb visitor pattern]
    (sesame-visit-pattern kb visitor pattern))
  (visit-pattern [kb visitor pattern options]
    (sesame-visit-pattern kb visitor pattern options))

  (construct-pattern [kb create-pattern pattern]
        (sesame-construct-pattern kb create-pattern pattern))
  (construct-pattern [kb create-pattern pattern options]
        (sesame-construct-pattern kb create-pattern pattern options))
  (construct-visit-pattern [kb visitor create-pattern pattern]
    (sesame-construct-visit-pattern kb visitor create-pattern pattern))
  (construct-visit-pattern [kb visitor create-pattern pattern options]
    (sesame-construct-visit-pattern kb visitor create-pattern pattern options))


  (ask-sparql [kb query-string]
    (sesame-ask-sparql kb query-string))
  (query-sparql [kb query-string]
    (sesame-query-sparql kb query-string))
  (count-sparql [kb query-string]
    (sesame-count-sparql kb query-string))
  (visit-sparql [kb visitor query-string]
    (sesame-visit-sparql kb visitor query-string))

  (construct-sparql [kb sparql-string]
    (sesame-construct-sparql kb sparql-string))
  (construct-visit-sparql [kb visitor sparql-string]
    (sesame-construct-visit-sparql kb visitor sparql-string))

  )

;; TODO factor this out to a "connection" package

;;; "constructors"
;;; --------------------------------------------------------

;; the way new SesameKBConnection is being called it isn't preserving
;;   the additional keys that are added on to the sesame server
;;   specifically the :value-factory

;; (defn new-sesame-server []
;;   (let [repository (HTTPRepository. *default-server* *repository-name*)]
;;     (.setPreferredTupleQueryResultFormat repository
;;                                          TupleQueryResultFormat/SPARQL)
;;     (if (and *username* *password*)
;;       (.setUsernameAndPassword repository *username* *password*))
;;     (assoc (SesameKB. repository (initial-ns-mappings) nil)
;;       :value-factory (.getValueFactory repository))))

(defn copy-dydra-slots [target-kb source-kb]
  (copy-rdf-slots (copy-kb-slots target-kb source-kb)
                  source-kb))


(defn dydra-kb-helper [repository]
  (initialize-ns-mappings
   (assoc (DydraKB. repository
                    (.getConnection repository)
		    *kb-features*)
     :value-factory (.getValueFactory repository))))


;; (defn new-sesame-server [& {:keys [server repo-name username password]
;;                             :or {server *default-server*
;;                                  repo-name *repository-name*
;;                                  username *username*
;;                                  password *password*}}]
;;   (println "server" server  " name" repo-name)
;;   (println "username" username  " password" password)
;;   (let [repository (HTTPRepository. server repo-name)]
;;     (.setPreferredTupleQueryResultFormat repository
;;                                          TupleQueryResultFormat/SPARQL)
;;     (if (and username password)
;;       (.setUsernameAndPassword repository username password))
;;     (assoc (SesameKB. repository (initial-ns-mappings) nil)
;;       :value-factory (.getValueFactory repository))))

(defn new-dydra-server [& {:keys [server repo-name username password]
                            :or {server *default-server*
                                 repo-name *repository-name*
                                 username *username*
                                 password *password*}}]
  ;; (println "server" server  " name" repo-name)
  ;; (println "username" username  " password" password)
  (assert repo-name "A repo-name is required.") 
  (let [repository (com.dydra.ndk.sesame.DydraRepository. repo-name)]
    (.initialize repository)
    ; (.setPreferredTupleQueryResultFormat repository TupleQueryResultFormat/SPARQL)
    (if (and username password)
      (.setUsernameAndPassword repository username password))
    (dydra-kb-helper repository)))

;; (defn new-sesame-server-helper [server & [repo-name username password]]
;;   (apply new-sesame-server
;;          (concat [:server server]
;;                  (if repo-name [:repo-name repo-name] nil)
;;                  (if username [:username username] nil)
;;                  (if password [:password password] nil))))


(defn ensure-dydra-connection
  "Ensure that the given kb binds an initialized (open) repository connection.
   If so, return it unchanged, otherwise re-open the repository and return a
   new DydraKB which comprises the new connection"
  [kb]
  (let [repository (:repository kb)]
    (if (.isInitialized repository)
      kb
      (do (.initialize repository)
          (copy-dydra-slots (assoc (DydraKB. repository
                                             (.getConnection repository)
                                             (features kb))
                                   :value-factory (:value-factory kb))
                            kb)))))

(defn close-dydra-connection
  "Ensure that the respective repository is closed and return a new
   DydraKB which releases the connection"
  [kb]
  (let [repository (:repository kb)]
    (when (.isInitialized repository)
      (.shutDown repository))
    (copy-dydra-slots (assoc (DydraKB. repository nil (features kb))
                             :value-factory (:value-factory kb))
                       kb)))


;(defmethod kb com.dydra.ndk.sesame.HTTPRepository [arg]
;  (if (class? arg)
;    (new-dydra-server)
;    (sesame-kb-helper arg)))


(defmethod kb com.dydra.ndk.sesame.DydraRepository [arg]
  (if (class? arg)
    (let [repo (com.dydra.ndk.sesame.DydraRepository. arg)]
      (.initialize repo)
      (dydra-kb-helper repo))
    (dydra-kb-helper arg)))

;; need to add more kb constructors in here for taking in various sesame objects
;;   repository, sail, etc. instances


(defn dydra-initialize [kb]
  (initialize-ns-mappings kb))

;; (defn new-sesame-memory-kb []
;;   (let [repo (SailRepository. (MemoryStore.))]
;;     (.initialize repo)
;;     ;(.setPreferredTupleQueryResultFormat repo TupleQueryResultFormat/SPARQL)
;;     (assoc (SesameKB. repo (initial-ns-mappings) nil)
;;       :value-factory (.getValueFactory repo))))

(defn new-dydra-native-kb []
  (kb com.dydra.ndk.sesame.DydraRepository))


(defmethod kb :dydra-native [_]
  (kb com.dydra.ndk.sesame.DydraRepository))


;;; --------------------------------------------------------
;;; END
;;; --------------------------------------------------------
