# LeanMass Calculator — Guide de Développement Complet

> **Projet académique** — ENSA-Agadir | Cours Mobile (Pr. Abderrazak Iazzi)  
> **Langage** : Kotlin | **IDE** : Android Studio Panda 4 | **Architecture** : MVVM  
> **Version** : 1.0.0 | **Min SDK** : API 24 (Android 7.0)

---

## Table des matières

1. [Choix technologique et justification](#1-choix-technologique)
2. [Mise en place de l'environnement](#2-environnement)
3. [Structure du projet GitHub](#3-github)
4. [Architecture du projet](#4-architecture)
5. [Étape 1 — Configuration et dépendances](#5-dependances)
6. [Étape 2 — Modèles de données](#6-modeles)
7. [Étape 3 — Base de données SQLite (Room)](#7-sqlite)
8. [Étape 4 — Firebase (Auth + Firestore)](#8-firebase)
9. [Étape 5 — Repository Pattern](#9-repository)
10. [Étape 6 — ViewModels](#10-viewmodels)
11. [Étape 7 — Interfaces utilisateur (XML)](#11-ui)
12. [Étape 8 — Activities et Fragments](#12-activities)
13. [Étape 9 — ViewBinding (variante)](#13-viewbinding)
14. [Étape 10 — Fichier de configuration](#14-config)
15. [Workflow GitHub recommandé](#15-github-workflow)
16. [Checklist de rendu](#16-checklist)

---

## 1. Choix technologique

**Kotlin** est le langage retenu pour les raisons suivantes :

- Langage officiel Android depuis 2017 (Google I/O)
- Enseigné dans le cours (voir supports du Pr. Iazzi)
- Null-safety native → moins de NullPointerException
- Coroutines intégrées pour les opérations asynchrones (SQLite, Firebase)
- Interopérable à 100% avec Java
- Support ViewBinding natif et élégant
- Syntaxe concise : data classes, extension functions, lambdas

---

## 2. Environnement de développement

### Prérequis

| Outil | Version recommandée |
|-------|-------------------|
| Android Studio | Panda 4 (2025.3.4) |
| Android SDK | API 36 (Android 16) |
| JDK | 17+ |
| Git | 2.x |
| Compte Firebase | gratuit (Spark plan) |
| Compte GitHub | gratuit |

### Installation Android Studio

1. Télécharger depuis [developer.android.com/studio](https://developer.android.com/studio)
2. Installer le SDK via SDK Manager → Android 7.0 minimum (API 24)
3. Créer un émulateur : Tools → Device Manager → Create Virtual Device → Pixel 6, API 33

---

## 3. Structure du projet GitHub

### Initialisation du dépôt

```bash
# Créer le dépôt sur GitHub (interface web ou CLI)
gh repo create LeanMassCalculator --public --description "Android app for Lean Body Mass tracking"

# Cloner localement
git clone https://github.com/VOTRE_USERNAME/LeanMassCalculator.git
cd LeanMassCalculator
```

### Fichier `.gitignore` (Android)

```
# Android
*.iml
.gradle
/local.properties
/.idea
.DS_Store
/build
/captures
.externalNativeBuild
.cxx
*.apk
*.aab

# Firebase — NE JAMAIS committer google-services.json avec de vraies clés
# Utiliser des variables d'environnement en CI/CD
google-services.json
```

> **Important** : Ajouter `google-services.json` au `.gitignore` ou utiliser
> un fichier `google-services.json.example` sans vraies clés pour le dépôt public.

### Branches Git recommandées

```
main          → code stable, prêt à livrer
develop       → intégration en cours
feature/auth  → authentification Firebase
feature/lbm   → calcul LBM + formules Boer
feature/history → historique + SQLite
feature/ui    → interfaces XML + ViewBinding
```

### Commandes Git quotidiennes

```bash
# Nouveau feature
git checkout -b feature/auth

# Sauvegarder le travail
git add .
git commit -m "feat(auth): add Firebase login with email/password"
git push origin feature/auth

# Fusionner dans develop
git checkout develop
git merge feature/auth

# Tag de version
git tag -a v1.0.0 -m "Version finale mini-projet"
git push --tags
```

### Convention de commits (Conventional Commits)

```
feat(module): description courte
fix(module): correction d'un bug
docs: mise à jour README
style: formatage, indentation
refactor: refactoring sans changement fonctionnel
test: ajout de tests
chore: mise à jour dépendances
```

---

## 4. Architecture du projet

### Structure des dossiers

```
app/src/main/
├── java/com/votrepackage/leanmass/
│   ├── data/
│   │   ├── local/
│   │   │   ├── AppDatabase.kt          # Room Database
│   │   │   ├── dao/
│   │   │   │   ├── UserDao.kt
│   │   │   │   └── LBMRecordDao.kt
│   │   │   └── entity/
│   │   │       ├── UserEntity.kt
│   │   │       └── LBMRecordEntity.kt
│   │   ├── remote/
│   │   │   └── FirestoreRepository.kt
│   │   └── repository/
│   │       └── LBMRepository.kt        # Source unique de vérité
│   ├── model/
│   │   ├── User.kt
│   │   └── LBMRecord.kt
│   ├── ui/
│   │   ├── auth/
│   │   │   ├── LoginActivity.kt
│   │   │   ├── RegisterActivity.kt
│   │   │   └── AuthViewModel.kt
│   │   ├── calculator/
│   │   │   ├── CalculatorActivity.kt
│   │   │   ├── ResultFragment.kt
│   │   │   └── LBMViewModel.kt
│   │   └── history/
│   │       ├── HistoryActivity.kt
│   │       ├── HistoryAdapter.kt
│   │       └── HistoryViewModel.kt
│   └── utils/
│       ├── Constants.kt
│       └── LBMCalculator.kt
├── res/
│   ├── layout/
│   │   ├── activity_login.xml
│   │   ├── activity_register.xml
│   │   ├── activity_calculator.xml
│   │   ├── activity_history.xml
│   │   ├── fragment_result.xml
│   │   └── item_history.xml
│   ├── values/
│   │   ├── strings.xml
│   │   ├── colors.xml
│   │   ├── themes.xml
│   │   └── constants.xml               # Seuils LBM configurables
│   └── drawable/
│       ├── ic_satisfied.xml            # Icône résultat satisfaisant
│       └── ic_unsatisfied.xml          # Icône résultat à surveiller
└── AndroidManifest.xml
```

---

## 5. Configuration et dépendances

### `build.gradle.kts` (Module :app)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.votrepackage.leanmass"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.votrepackage.leanmass"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        viewBinding = true  // Active ViewBinding
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Architecture (ViewModel + LiveData)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.4")
    implementation("androidx.activity:activity-ktx:1.9.1")

    // Room (SQLite)
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
}
```

---

## 6. Modèles de données

### `LBMRecord.kt` — Modèle principal

```kotlin
package com.votrepackage.leanmass.model

data class LBMRecord(
    val id: String = "",
    val userId: String = "",
    val weight: Double = 0.0,        // Poids en kg
    val height: Double = 0.0,        // Taille en cm
    val gender: String = "male",     // "male" ou "female"
    val lbmResult: Double = 0.0,     // Résultat calculé en kg
    val isSatisfactory: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
```

### `User.kt`

```kotlin
package com.votrepackage.leanmass.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = ""
)
```

---

## 7. Base de données SQLite (Room)

### `LBMRecordEntity.kt`

```kotlin
package com.votrepackage.leanmass.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lbm_records")
data class LBMRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val weight: Double,
    val height: Double,
    val gender: String,
    val lbmResult: Double,
    val isSatisfactory: Boolean,
    val timestamp: Long
)
```

### `LBMRecordDao.kt`

```kotlin
package com.votrepackage.leanmass.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.votrepackage.leanmass.data.local.entity.LBMRecordEntity

@Dao
interface LBMRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: LBMRecordEntity)

    @Query("SELECT * FROM lbm_records WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllRecords(userId: String): LiveData<List<LBMRecordEntity>>

    @Delete
    suspend fun deleteRecord(record: LBMRecordEntity)

    @Query("DELETE FROM lbm_records WHERE id = :id")
    suspend fun deleteById(id: Int)
}
```

### `AppDatabase.kt`

```kotlin
package com.votrepackage.leanmass.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.votrepackage.leanmass.data.local.dao.LBMRecordDao
import com.votrepackage.leanmass.data.local.entity.LBMRecordEntity

@Database(entities = [LBMRecordEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun lbmRecordDao(): LBMRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "leanmass_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

---

## 8. Firebase (Auth + Firestore)

### Configuration Firebase

1. Aller sur [console.firebase.google.com](https://console.firebase.google.com)
2. Créer un projet : **LeanMassCalculator**
3. Ajouter une application Android avec votre `applicationId`
4. Télécharger `google-services.json` → placer dans `app/`
5. Activer **Authentication** → Email/Password
6. Activer **Firestore Database** → mode test

### Structure Firestore

```
users/
  {uid}/
    profile: { email, displayName, createdAt }
    records/
      {recordId}/
        weight, height, gender, lbmResult,
        isSatisfactory, timestamp
```

### `FirestoreRepository.kt`

```kotlin
package com.votrepackage.leanmass.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.votrepackage.leanmass.model.LBMRecord
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private fun recordsCollection(userId: String) =
        db.collection("users").document(userId).collection("records")

    suspend fun saveRecord(record: LBMRecord): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Non connecté"))
            val data = hashMapOf(
                "weight" to record.weight,
                "height" to record.height,
                "gender" to record.gender,
                "lbmResult" to record.lbmResult,
                "isSatisfactory" to record.isSatisfactory,
                "timestamp" to record.timestamp
            )
            recordsCollection(uid).add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecords(userId: String): Result<List<LBMRecord>> {
        return try {
            val snapshot = recordsCollection(userId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().await()
            val records = snapshot.documents.mapNotNull { doc ->
                LBMRecord(
                    id = doc.id,
                    userId = userId,
                    weight = doc.getDouble("weight") ?: 0.0,
                    height = doc.getDouble("height") ?: 0.0,
                    gender = doc.getString("gender") ?: "male",
                    lbmResult = doc.getDouble("lbmResult") ?: 0.0,
                    isSatisfactory = doc.getBoolean("isSatisfactory") ?: false,
                    timestamp = doc.getLong("timestamp") ?: 0L
                )
            }
            Result.success(records)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRecord(userId: String, recordId: String): Result<Unit> {
        return try {
            recordsCollection(userId).document(recordId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

---

## 9. Repository Pattern

### `LBMRepository.kt` — Source unique de vérité

```kotlin
package com.votrepackage.leanmass.data.repository

import android.content.Context
import com.votrepackage.leanmass.data.local.AppDatabase
import com.votrepackage.leanmass.data.local.entity.LBMRecordEntity
import com.votrepackage.leanmass.data.remote.FirestoreRepository
import com.votrepackage.leanmass.model.LBMRecord

enum class PersistenceMode { LOCAL, CLOUD }

class LBMRepository(context: Context) {

    private val localDao = AppDatabase.getDatabase(context).lbmRecordDao()
    private val firestoreRepo = FirestoreRepository()

    var persistenceMode: PersistenceMode = PersistenceMode.LOCAL

    // Sauvegarder un calcul
    suspend fun saveRecord(record: LBMRecord, userId: String): Result<Unit> {
        return when (persistenceMode) {
            PersistenceMode.LOCAL -> {
                val entity = record.toEntity(userId)
                localDao.insertRecord(entity)
                Result.success(Unit)
            }
            PersistenceMode.CLOUD -> firestoreRepo.saveRecord(record)
        }
    }

    // Récupérer l'historique (LiveData pour SQLite)
    fun getLocalRecords(userId: String) = localDao.getAllRecords(userId)

    // Récupérer depuis Firestore
    suspend fun getCloudRecords(userId: String) = firestoreRepo.getRecords(userId)

    // Supprimer
    suspend fun deleteLocalRecord(entity: LBMRecordEntity) = localDao.deleteRecord(entity)
    suspend fun deleteCloudRecord(userId: String, recordId: String) =
        firestoreRepo.deleteRecord(userId, recordId)

    // Extension de conversion
    private fun LBMRecord.toEntity(userId: String) = LBMRecordEntity(
        userId = userId,
        weight = weight,
        height = height,
        gender = gender,
        lbmResult = lbmResult,
        isSatisfactory = isSatisfactory,
        timestamp = timestamp
    )
}
```

---

## 10. ViewModels

### `LBMCalculator.kt` — Formules de Boer

```kotlin
package com.votrepackage.leanmass.utils

object LBMCalculator {

    // Formule de Boer
    fun calculate(weight: Double, height: Double, gender: String): Double {
        return when (gender.lowercase()) {
            "male"   -> (0.407 * weight) + (0.267 * height) - 19.2
            "female" -> (0.252 * weight) + (0.473 * height) - 48.3
            else     -> throw IllegalArgumentException("Genre invalide: $gender")
        }
    }

    fun isSatisfactory(lbm: Double, gender: String, minMale: Double, minFemale: Double): Boolean {
        return when (gender.lowercase()) {
            "male"   -> lbm >= minMale
            "female" -> lbm >= minFemale
            else     -> false
        }
    }
}
```

### `LBMViewModel.kt`

```kotlin
package com.votrepackage.leanmass.ui.calculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.votrepackage.leanmass.data.repository.LBMRepository
import com.votrepackage.leanmass.model.LBMRecord
import com.votrepackage.leanmass.utils.Constants
import com.votrepackage.leanmass.utils.LBMCalculator
import kotlinx.coroutines.launch

class LBMViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LBMRepository(application)

    private val _currentRecord = MutableLiveData<LBMRecord>()
    val currentRecord: LiveData<LBMRecord> = _currentRecord

    private val _saveStatus = MutableLiveData<Result<Unit>>()
    val saveStatus: LiveData<Result<Unit>> = _saveStatus

    fun calculate(weight: Double, height: Double, gender: String) {
        val lbm = LBMCalculator.calculate(weight, height, gender)
        val isSat = LBMCalculator.isSatisfactory(
            lbm, gender,
            Constants.LBM_MIN_MALE,
            Constants.LBM_MIN_FEMALE
        )
        _currentRecord.value = LBMRecord(
            weight = weight,
            height = height,
            gender = gender,
            lbmResult = lbm,
            isSatisfactory = isSat
        )
    }

    fun saveRecord(userId: String) {
        val record = _currentRecord.value ?: return
        viewModelScope.launch {
            val result = repository.saveRecord(record, userId)
            _saveStatus.value = result
        }
    }
}
```

### `AuthViewModel.kt`

```kotlin
package com.votrepackage.leanmass.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<FirebaseUser?>()
    val authState: LiveData<FirebaseUser?> = _authState

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        _authState.value = auth.currentUser
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                _authState.value = result.user
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                _authState.value = result.user
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun logout() {
        auth.signOut()
        _authState.value = null
    }
}
```

---

## 11. Interfaces utilisateur (XML)

### `activity_calculator.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:gap="16dp">

        <TextView
            android:text="@string/title_calculator"
            android:textSize="24sp"
            android:textStyle="bold"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"/>

        <!-- Sélection du genre -->
        <RadioGroup
            android:id="@+id/rgGender"
            android:orientation="horizontal"
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

            <RadioButton
                android:id="@+id/rbMale"
                android:text="@string/label_male"
                android:checked="true"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"/>

            <RadioButton
                android:id="@+id/rbFemale"
                android:text="@string/label_female"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"/>
        </RadioGroup>

        <!-- Poids -->
        <com.google.android.material.textfield.TextInputLayout
            android:hint="@string/hint_weight"
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/etWeight"
                android:inputType="numberDecimal"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"/>
        </com.google.android.material.textfield.TextInputLayout>

        <!-- Taille -->
        <com.google.android.material.textfield.TextInputLayout
            android:hint="@string/hint_height"
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/etHeight"
                android:inputType="numberDecimal"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"/>
        </com.google.android.material.textfield.TextInputLayout>

        <Button
            android:id="@+id/btnCalculate"
            android:text="@string/btn_calculate"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"/>

        <!-- Fragment résultat intégré -->
        <androidx.fragment.app.FragmentContainerView
            android:id="@+id/resultFragment"
            android:name="com.votrepackage.leanmass.ui.calculator.ResultFragment"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:visibility="gone"/>

    </LinearLayout>
</ScrollView>
```

### `fragment_result.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp"
    android:gravity="center">

    <ImageView
        android:id="@+id/ivResultIcon"
        android:layout_width="64dp"
        android:layout_height="64dp"
        android:contentDescription="@string/cd_result_icon"/>

    <TextView
        android:id="@+id/tvLBMValue"
        android:textSize="32sp"
        android:textStyle="bold"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>

    <TextView
        android:id="@+id/tvResultLabel"
        android:textSize="18sp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>

    <Button
        android:id="@+id/btnSave"
        android:text="@string/btn_save"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>

</LinearLayout>
```

---

## 12. Activities et Fragments

### `CalculatorActivity.kt` — SANS ViewBinding

```kotlin
package com.votrepackage.leanmass.ui.calculator

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.votrepackage.leanmass.R
import android.widget.Button
import android.widget.RadioGroup

class CalculatorActivity : AppCompatActivity() {

    private val viewModel: LBMViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        val rgGender = findViewById<RadioGroup>(R.id.rgGender)
        val etWeight = findViewById<TextInputEditText>(R.id.etWeight)
        val etHeight = findViewById<TextInputEditText>(R.id.etHeight)
        val btnCalculate = findViewById<Button>(R.id.btnCalculate)

        btnCalculate.setOnClickListener {
            val weight = etWeight.text.toString().toDoubleOrNull() ?: return@setOnClickListener
            val height = etHeight.text.toString().toDoubleOrNull() ?: return@setOnClickListener
            val gender = if (rgGender.checkedRadioButtonId == R.id.rbMale) "male" else "female"
            viewModel.calculate(weight, height, gender)
        }

        viewModel.currentRecord.observe(this) { record ->
            // Mettre à jour le ResultFragment
        }
    }
}
```

---

## 13. ViewBinding (variante avec ViewBinding)

### `CalculatorActivityBinding.kt` — AVEC ViewBinding

```kotlin
package com.votrepackage.leanmass.ui.calculator

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.votrepackage.leanmass.databinding.ActivityCalculatorBinding

class CalculatorActivityBinding : AppCompatActivity() {

    // ViewBinding : accès typé aux vues, plus de findViewById
    private lateinit var binding: ActivityCalculatorBinding
    private val viewModel: LBMViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflation via ViewBinding
        binding = ActivityCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalculate.setOnClickListener {
            val weight = binding.etWeight.text.toString().toDoubleOrNull() ?: return@setOnClickListener
            val height = binding.etHeight.text.toString().toDoubleOrNull() ?: return@setOnClickListener
            val gender = if (binding.rgGender.checkedRadioButtonId == R.id.rbMale) "male" else "female"
            viewModel.calculate(weight, height, gender)
        }

        viewModel.currentRecord.observe(this) { record ->
            binding.resultFragment.visibility = android.view.View.VISIBLE
            // Les données sont propagées via ViewModel partagé
        }
    }
}
```

### Comparaison ViewBinding

| Aspect | Sans ViewBinding | Avec ViewBinding |
|--------|-----------------|-----------------|
| Accès aux vues | `findViewById<Button>(R.id.btn)` | `binding.btn` |
| Null safety | Risque de NullPointerException | Compile-time safe |
| Type safety | Cast manuel requis | Types automatiques |
| Performance | Identique | Identique |
| Code | Plus verbeux | Plus concis |

---

## 14. Fichier de configuration

### `res/values/constants.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Seuils LBM configurables — méthode de Boer -->
    <item name="lbm_min_male" format="float" type="dimen">38.0</item>
    <item name="lbm_min_female" format="float" type="dimen">24.0</item>

    <!-- Messages -->
    <string name="result_satisfactory">Résultat satisfaisant ✓</string>
    <string name="result_unsatisfactory">Résultat à surveiller ⚠</string>
</resources>
```

### `Constants.kt`

```kotlin
package com.votrepackage.leanmass.utils

object Constants {
    // Seuils par défaut (peuvent être lus depuis constants.xml via Context)
    const val LBM_MIN_MALE   = 38.0  // kg
    const val LBM_MIN_FEMALE = 24.0  // kg
}
```

---

## 15. Workflow GitHub recommandé

### README.md du dépôt

```markdown
# LeanMass Calculator 💪

Application Android permettant de calculer et suivre la masse maigre (LBM)
en utilisant la méthode de Boer.

## Fonctionnalités
- Authentification sécurisée (Firebase Auth)
- Calcul LBM (formule de Boer — Homme & Femme)
- Retour visuel immédiat (satisfaisant / à surveiller)
- Historique avec suppression (SQLite local + Firebase Cloud)
- Interfaces avec et sans ViewBinding

## Stack technique
- Kotlin + Android Studio
- Architecture MVVM + LiveData
- Room (SQLite) / Firebase Firestore
- ViewBinding

## Installation
1. Cloner le dépôt
2. Ouvrir dans Android Studio
3. Ajouter `google-services.json` (Firebase console)
4. Build & Run

## Structure du projet
Voir ARCHITECTURE.md

## Auteur
Votre Nom — ENSA-Agadir
```

### Commandes Git pour le rendu final

```bash
# Finaliser sur main
git checkout main
git merge develop
git tag -a v1.0.0 -m "Mini-projet LeanMass Calculator — Version finale"
git push origin main --tags

# Vérifier le dépôt
git log --oneline --graph --all
```

---

## 16. Checklist de rendu

### Fonctionnalités obligatoires

- [ ] Inscription avec email + mot de passe (Firebase Auth)
- [ ] Connexion sécurisée
- [ ] Formulaire de calcul : poids, taille, genre
- [ ] Calcul LBM via formule de Boer (Homme et Femme)
- [ ] Icône satisfait / insatisfait avec message
- [ ] Sauvegarde dans SQLite (Room)
- [ ] Sauvegarde dans Firebase Firestore
- [ ] Historique : liste des calculs passés
- [ ] Suppression d'un enregistrement
- [ ] Au moins 2 variantes d'interface (avec/sans ViewBinding)
- [ ] Fichier `constants.xml` avec seuils ajustables

### Qualité du code

- [ ] Architecture MVVM respectée
- [ ] Pas de logique métier dans les Activity/Fragment
- [ ] Gestion des erreurs (try/catch, Result)
- [ ] Commentaires sur les parties complexes

### GitHub

- [ ] Dépôt public avec README complet
- [ ] Au moins 10 commits significatifs avec messages clairs
- [ ] Branches feature/* utilisées
- [ ] `.gitignore` correct (pas de `google-services.json` sensible)
- [ ] Tag `v1.0.0` sur le commit final

---

*Guide rédigé pour le cours Développement Mobile — ENSA-Agadir (Pr. Abderrazak Iazzi)*  
*Langage : Kotlin | Architecture : MVVM | Android Studio Panda 4*
