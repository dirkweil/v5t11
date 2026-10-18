## Runtime Environment
- Die Microservices nutzen eine gemeinsame Datenbank, die im Kubernetes läuft. Keine neue Instanz starten!
- Die Microservices nutzen einen gemeinsamen Kafka-Broker, der im Kubernetes läuft. Keine neue Instanz starten!
- Keine Anwendungen im Dev Mode starten, sondern Nutzer darum bitten.

## Git
- Keine schreibenden Zugriffe (add/rm/reset/commit/...), sondern Nutzer darum bitten.
- Keine Branch-Operationen (branch/merge/rebase/...), sondern Nutzer darum bitten.
- Keine Remote-Operationen (push/pull/fetch/clone/...), sondern Nutzer darum bitten.
