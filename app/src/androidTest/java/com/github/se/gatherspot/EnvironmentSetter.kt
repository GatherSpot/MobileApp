package com.github.se.gatherspot

import android.util.Log
import com.github.se.gatherspot.model.Profile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest

class EnvironmentSetter {
  companion object {

      val profileFirebaseConnection = ProfileFirebaseConnection()

    /** This function logs in the user for testing purposes this user has his email verified */
    fun testLogin() {

      runTest {
        val TAG = "testLogin"
        // Hard coded this is the one account easily logged in for all tests unless specified
        // otherwise
        async {
              Firebase.auth.createUserWithEmailAndPassword(
                  "neverdeleted@mail.com", "GatherSpot,2024;")
            }
            .await()
        Log.d(TAG, "Logged in")
      }
    }

      fun testLoginCleanUp(){
            runTest {
                if(Firebase.auth.currentUser != null)
                    async {Firebase.auth.currentUser!!.delete()}.await()
            }
      }

      fun signUpErrorSetUp(){
          runTest{
              //Make sure the "test" username is already in use
              async{ ProfileFirebaseConnection().add(Profile("test","","","t_SignUpError", setOf()))}.await()
          }
      }

      fun signUpSetUp(userName : String, email : String){
            runTest{

                async {
                    //Make sure the username is not in use
                    var toDelete : Profile? = null
                    async { toDelete = profileFirebaseConnection.fetchFromUserName(userName)}.await()
                    if(toDelete != null)
                        profileFirebaseConnection.delete(toDelete!!.id)}.await()

                    //Make sure the email is not in use
                    try {

                            Firebase.auth.createUserWithEmailAndPassword(
                                email,
                                "to be Deleted 128 okay"
                            ).await()
                    }catch(e: FirebaseAuthUserCollisionException){
                        Log.d("testsignUpSetUp", "User already exists you need to delete them manually from the database")
                        return@runTest //If the user already exists we can't do anything from here
                    } catch (e : FirebaseAuthInvalidCredentialsException){
                        Log.d("testsignUpSetUp", "Invalid email")
                        return@runTest
                    }


                    //We just created a user with the email so now we delete him
                    if (Firebase.auth.currentUser == null)
                        async{Firebase.auth.signInWithEmailAndPassword(email, "to be Deleted 128 okay")}.await()
                    async{Firebase.auth.currentUser!!.delete()}.await()




            }
      }

      fun signUpCleanUp(userName: String) {
            runTest {
                if(Firebase.auth.currentUser != null)
                    async {Firebase.auth.currentUser!!.delete()}.await()
                val toDelete : Profile? = profileFirebaseConnection.fetchFromUserName(userName)
                if(toDelete != null)
                    async {profileFirebaseConnection.delete(toDelete.id)}.await()
            }
      }
  }
}
