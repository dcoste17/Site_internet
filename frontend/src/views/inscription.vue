<template>

  <div class="inscription">
    <p>Creez vos identifiants maintenant</p>
    <div>
        <label for="nom_utilisateur">Nom d'utilisateur  : </label>
        <input type="text" id="nom_utilisateur" name="user_name">
    </div>
    <h3> INSCRIPTION </h3>
    <br>
    <div>
        <label for="mot_de_passe">Mot de passe  : </label>
        <input type="text" id="mot-de-passe" name="mot-de-passe">
    </div>
    <br>
    <button class="Search__button" @click="inscription()">S'inscrire</button>
  </div>
</template>
<script>
import axios from "axios";

export default {
  name: "inscription",
  props: {
    msg: String,
  },
  data() {
    return {
      response: [] ,
      errors: [],
      file:"",
      imageId:null,
    };
  },
  methods: {
    inscription(){
      var user= document.getElementById('nom_utilisateur').value;
      var pswd= document.getElementById('mot-de-passe').value;
      alert("ahaha");
      axios
        .post("/inscription", { params: { user: user, pswd: pswd}}, { responseType:"blob" })
        .then((response) => {
          alert(user);
          location.href = "http://localhost:8089/";
          
        })
        .catch((e) => {
          this.errors.push(e);
        });
  },
        
    },
}
</script>


