# Application Android - CalculIMC (Kotlin)

Projet Android Studio développé en **Kotlin** pour le calcul et l'interprétation de l'Indice de Masse Corporelle (IMC).

## 🚀 Fonctionnalités

- **Calcul précis de l'IMC** ($\text{IMC} = \text{poids} / \text{taille}^2$) arrondi à 2 decimales.
- **Catégorisation automatique** avec changement de couleur dynamique :
  - **Insuffisance pondérale** ($< 18.5$) ➔ Orange
  - **Corpulence normale** ($18.5 \le IMC < 25$) ➔ Vert
  - **Surpoids** ($25 \le IMC < 30$) ➔ Orange
  - **Obésité modérée** ($30 \le IMC < 35$) ➔ Rouge
  - **Obésité sévère** ($35 \le IMC < 40$) ➔ Rouge
  - **Obésité morbide** ($\ge 40$) ➔ Rouge foncé
- **Bouton Effacer** qui réinitialise les champs et repositionne le curseur sur le poids.
- **Gestion stricte des erreurs** (champs vides, valeurs négatives ou nulles, virgules et points décimaux).
- **Améliorations bonus** :
  - Sélecteur d'unités (m et cm).
  - Bouton de partage des résultats (`Intent.ACTION_SEND`).
  - Sauvegarde de l'état lors de la rotation de l'écran.
  - Historique complet des calculs récents (`RecyclerView`).

## 📁 Structure du Projet

```text
CalculIMC/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/calculimc/
│   │   │   ├── MainActivity.kt
│   │   │   ├── ImcRecord.kt
│   │   │   └── HistoryAdapter.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml
│   │   │   │   └── item_history.xml
│   │   │   └── values/
│   │   │       ├── strings.xml
│   │   │       ├── colors.xml
│   │   │       └── themes.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── COMPTE_RENDU.md
├── build.gradle.kts
└── settings.gradle.kts
```

## 🛠️ Instruction pour ouvrir le projet dans Android Studio

1. Ouvrez Android Studio.
2. Sélectionnez **Open** et choisissez le dossier `yassinebouferguinetpIMC`.
3. Laissez Gradle synchroniser le projet.
4. Lancez l'application sur un émulateur ou un appareil Android connecté.
