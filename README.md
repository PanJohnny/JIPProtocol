# Jednoduchý Internetový Protokol
## Anotace
Tato práce má za cíl navrhnout a vytvořit internetový protokol. Práce může sloužit nejen jako pomůcka pro programátory, kteří ji mohou využít pro vytvoření vlastního protokolu, ale i pro širokou veřejnost, která si z ní může odnést zajímavé informace o fungování internetu. Práce obsahuje detailní popis návrhu a implementace mého vlastního protokolu postaveném na TCP a inspirovaném HTTP/1.0. Využil jsem programovacího jazyku Java a vestavěných knihoven java.io, java.net,  javax.crypto a java.security. Důraz je kladen i na zabezpečení přenosu dat, a to pomocí šifrovacích algoritmů AES a RSA.

## Annotation
This work aims to design and develop an internet protocol. The work can serve not only as a tool for programmers, who can use it to create their own protocol, but also for the general public, who can learn interesting information about the workings of the Internet. The paper contains a detailed description of the design and implementation of my own protocol based on TCP and inspired by HTTP/1.0. I have used the Java programming language and the built-in libraries java.io, java.net, javax.crypto and java.security. Emphasis is also placed on the security of data transmission, using the AES and RSA encryption algorithms.

## Struktura žádosti
```
VERZE /cesta/k/dokumentu
Hlavička: Hodnota
Hlavička2: Hodnota

Tělo žádosti
```

## Struktura odpovědi
```
VERZE 
Status: OK
Hlavička: Hlavička

Tělo odpovědi
```