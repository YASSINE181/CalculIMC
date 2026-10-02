# Compte Rendu - TP Android Studio : Calcul de l'IMC

**Application :** CalculIMC  
**Langage :** Kotlin  
**SDK Minimum :** API 24 (Android 7.0 Nougat)  
**Architecture :** View Binding + Material Design 3  

---

## 1. Contexte & Objectifs

L'objectif de ce TP est de réaliser une application Android complète en Kotlin permettant d'évaluer l'Indice de Masse Corporelle (IMC) d'un utilisateur à partir de son poids (en kg) et de sa taille (en m ou cm).

Les objectifs pédagogiques maîtrisés au cours de ce projet sont :
- Conception d'interface utilisateur réactive avec Material Design 3.
- Manipulation des widgets `EditText`, `TextView`, `Button`, `MaterialButtonToggleGroup`, et `RecyclerView`.
- Traitement de la saisie utilisateur et gestion dynamique de la localisation (virgule décimale vs point).
- Validation robuste des données et messages d'erreur intuitifs sans plantage d'application.
- Structure conditionnelle pour la catégorisation et l'application dynamique des couleurs de statut.
- Sauvegarde de l'état de l'application lors de la rotation d'écran.

---

## 2. Principe du Calcul & Formule

L'IMC est calculé selon la formule standard :

$$\text{IMC} = \frac{\text{poids (kg)}}{\text{taille (m)} \times \text{taille (m)}}$$

Le résultat est ensuite arrondi à deux chiffres après la virgule.

---

## 3. Matrice de Catégorisation et Couleurs

| Plage de l'IMC | Catégorie Affichee | Couleur Associée | Hex de la couleur |
|---|---|---|---|
| $< 18.5$ | Insuffisance pondérale | Orange | `#FF9800` |
| $18.5 \le \text{IMC} < 25$ | Corpulence normale | Vert | `#4CAF50` |
| $25 \le \text{IMC} < 30$ | Surpoids | Orange | `#FF9800` |
| $30 \le \text{IMC} < 35$ | Obésité modérée | Rouge | `#F44336` |
| $35 \le \text{IMC} < 40$ | Obésité sévère | Rouge | `#D32F2F` |
| $\ge 40$ | Obésité morbide | Rouge foncé | `#8B0000` |

---

## 4. Résultats des Tests Effectués (Section 6 du Sujet)

Les tests suivants ont été exécutés et validés conformément au tableau de test du TP :

| Poids Saisi | Taille Saisie | Résultat Obtenu | Catégorie Obtenue | Statut du Test |
|---|---|---|---|---|
| **70 kg** | **1.75 m** | **22.86** | Corpulence normale | ✅ Conforme |
| **50 kg** | **1.75 m** | **16.33** | Insuffisance pondérale | ✅ Conforme |
| **85 kg** | **1.70 m** | **29.41** | Surpoids | ✅ Conforme |
| **100 kg** | **1.70 m** | **34.60** | Obésité modérée | ✅ Conforme |
| Champ vide | 1.75 m | Aucun calcul | Message : "Veuillez entrer votre poids" | ✅ Conforme |
| 70 kg | Champ vide | Aucun calcul | Message : "Veuillez entrer votre taille" | ✅ Conforme |
| 70 kg | 0 m | Aucun calcul | Message : "La taille doit être un nombre positif supérieur à 0" | ✅ Conforme |

---

## 5. Améliorations Facultatives Implémentées (Section 8)

1. **Jauge & Badge Coloré Réactif :**  
   Affichage dynamique de la catégorie sous forme d'un badge arrondi dont le fond prend la couleur correspondant au niveau d'IMC.
2. **Conservation de l'État après Rotation :**  
   Utilisation de `onSaveInstanceState` pour restaurer la valeur calculée, la catégorie, la couleur et le choix de l'unité lors des rotations de l'écran.
3. **Bouton de Partage des Résultats :**  
   Un bouton "Partager le résultat" permet d'envoyer le résultat (IMC + catégorie) vers d'autres applications (WhatsApp, Email, SMS) via un `Intent.ACTION_SEND`.
4. **Historique des Calculs (`RecyclerView`) :**  
   Chaque calcul est automatiquement enregistré dans un historique visuel avec couleur et détails, qu'il est possible de réinitialiser.
5. **Sélecteur d'Unité (m / cm) :**  
   Possibilité de choisir entre mètres et centimètres pour la saisie de la taille, avec conversion automatique en mètres pour le calcul.

---

## 6. Difficultés Rencontrées et Solutions

1. **Gestion des séparateurs décimaux (Virgule vs Point) :**  
   *Problème :* Selon la langue du clavier virtuel (Français), l'utilisateur peut entrer une virgule (`1,75`) provoquant un crash si elle est directement convertie via `toDouble()`.  
   *Solution :* Utilisation de `.replace(',', '.').toDoubleOrNull()` pour garantir une conversion robuste.
2. **Validation des entrées sans plantage :**  
   *Problème :* Empêcher la division par zéro ou le calcul avec des valeurs négatives.  
   *Solution :* Vérification explicite `valeur <= 0` avec attribution d'erreurs graphiques via `TextInputLayout.setError()`.

---

## 7. Structuration des Fichiers Clés du Projet

- [`MainActivity.kt`](file:///c:/Users/Ghada/OneDrive/Desktop/yassinebouferguinetpIMC/app/src/main/java/com/example/calculimc/MainActivity.kt) : Logique de l'application en Kotlin.
- [`activity_main.xml`](file:///c:/Users/Ghada/OneDrive/Desktop/yassinebouferguinetpIMC/app/src/main/res/layout/activity_main.xml) : Interface graphique réactive Material 3.
- [`strings.xml`](file:///c:/Users/Ghada/OneDrive/Desktop/yassinebouferguinetpIMC/app/src/main/res/values/strings.xml) : Centralisation de tous les textes réutilisables.
- [`colors.xml`](file:///c:/Users/Ghada/OneDrive/Desktop/yassinebouferguinetpIMC/app/src/main/res/values/colors.xml) : Palette de couleurs du projet et des catégories IMC.
