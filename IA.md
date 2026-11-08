Voici les problèmes que j'ai rencontrés et j'ai utilisé L'IA pour les résoudre.
1. Incompatibilité de dépendances et conflits de versions dans pom.xml
   Le problème : Lors de l'ajout des dépendances pour le parser PDF (ApacheTikaDocumentParser), le modèle d'embeddings local (BgeSmallEnV15) ou le module langchain4j-embeddings-bge-small-en-v15:1.22.0, Maven renvoyait des erreurs d'artefacts introuvables (Could not find artifact...) ou des erreurs d'exécution de type java.lang.NoClassDefFoundError: dev/langchain4j/model/embedding/TokenCountEstimator à cause de conflits entre les versions 1.22.0 et 1.0.0-beta1.

La solution :

Nous avons supprimé les dépendances problématiques d'Apache Tika et BGE Small.

Nous sommes repassés sur l'architecture standard 100% Google GenAI (GoogleGenAiEmbeddingModel et GoogleGenAiChatModel) nativement intégrée et parfaitement compatible avec la version 1.22.0 de LangChain4j.

2. Dépassement des quotas gratuits d'API Gemini (Erreur HTTP 429 RESOURCE_EXHAUSTED)
   Le problème : Lors du chargement et de l'indexation de votre fichier PDF de cours dans le Test 6, l'outil EmbeddingStoreIngestor découpait le document en de très nombreux petits morceaux. L'envoi simultané de tous ces morceaux dépassait la limite du Tier Gratuit de Google Gemini (30 000 tokens d'embeddings par minute).

La solution :

Nous avons ajusté la taille du découpage en grands blocs de texte de 5 000 caractères avec la méthode DocumentSplitters.recursive(5000, 200).

Ce réglage a permis de diviser la totalité du PDF en un nombre très réduit de segments (moins de 10), garantissant ainsi le respect strict des quotas par minute sans aucune erreur 429.

3. Fichier PDF introuvable au runtime
   Le problème : Le programme s'arrêtait immédiatement en signalant que le fichier PDF d'entrée était introuvable dans le projet Java.

La solution :

Nous avons ajouté une vérification automatique de l'existence du fichier cours.pdf à la racine du projet avec Files.isRegularFile(...).

Nous avons configuré l'accès au chemin absolu du fichier (pdfPath.toAbsolutePath()) pour garantir que FileSystemDocumentLoader puisse le charger sans erreur de chemin sous Windows.

4. Conservation des données d'un ancien test (Réponse résiduelle "Le chat de Pierre s'appelle Felix")
   Le problème : Au lancement initial de Test6Rag2.java, la console affichait encore la réponse du Test 5 au lieu de lancer la conversation interactive sur le fichier PDF.

La solution :

Nous avons nettoyé les appels de méthodes résiduels qui lisaient l'ancien fichier infos.txt.

Nous avons réécrit proprement la méthode interactive conversationAvec(Assistant assistant) basée sur une boucle Scanner, permettant de poser des questions dynamiquement sur le PDF et d'arrêter le programme avec le mot-clé "fin".

5. Avertissements sur le nommage des paramètres des outils (Parameter 'arg0' of tool method...)
   Le problème : Dans le Test 7, lors du chargement des méthodes de la classe MeteoTool, LangChain4j affichait un avertissement indiquant que les paramètres étaient vus sous le nom générique arg0 par le LLM.

La solution :

Nous avons annoté explicitement les paramètres des méthodes Java avec @P("Description...") dans la classe MeteoTool pour donner des indications claires et descriptives au modèle Gemini lors de la génération des requêtes JSON.