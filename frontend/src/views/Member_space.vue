<template>
  <div class="member_space">
    <h1>Member space</h1>
    <p>Entrez votre nom d'utilisateur pour vous connecter à votre compte</p>
    <div>
        <label for="nom_utilisateur">Nom d'utilisateur  : </label>
        <input type="text" id="nom_utilisateur" name="user_name">
    </div>
    <br>
    <div>
        <label for="mot_de_passe">Mot de passe  : </label>
        <input type="text" id="mot-de-passe" name="mot-de-passe">
    </div>
    <br>
    <button class="Search__button" @click="connexion()">Se connecter</button>
  </div>
    <br>
    <p> Vous n'avez pas encore de compte ? </p> <a href="http://localhost:8089/#/inscription" target="_blank" rel="noopener">Inscrivez-vous gratuitement</a>
</template>
<script>
import axios from "axios";

export default {
  name: "Member_space",
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
    connexion(){
      var user= document.getElementById('nom_utilisateur').value;
      var pswd= document.getElementById('mot-de-passe').value;
      alert("ahaha");
      axios
        .get("/member_space", { params: { user: user, pswd: pswd}}, { responseType:"blob" })
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


