<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    <button class="Search__button" @click="mounted()">Choose an image</button>
    
    <select v-model="imageId" id="liste" @change="mselect()">
      <option> </option>
           <option v-for="values in response" :key="values.id" :value="values.Id"> {{values}} </option>
        </select>
      <br/>
      <img alt="" id="val"/><br/>
   <select hidden="hidden" id="liste3" @change="applyFilters()">
      <option> </option>
           <option values = "blueFilter"> blueFilter </option>
           <option values = "augDimLuminosite_cursor" > augDimLuminosite_cursor </option>
           <option values = "conversionCG"> conversionCG </option>
           <option values = "conversionrgbToHsv"> conversionrgbToHsv </option>
           <option values = "egalisationHistogramme"> egalisationHistogramme </option>
           <option values = "dynamicImageMinMax"> dynamicImageMinMax </option>
        </select>
       <br/>
        <button class="Search__button" @click="rename()">Rename</button>
        <button class="Search__button" @click="deleted()">Delete</button>
        <button class="Search__button" @click="download()">Download</button>
      <br/>

      <div class="container">
        <div class="large-12 medium-12 small-12 cell">
          <label>File
            <input type="file" id="file" ref="file" v-on:change="handleFileUpload()"/>
          </label>
            <button v-on:click="submitFile()">Submit</button>
        </div>
      </div><br/>
        <h3>{{res}}</h3>
        <button class="Search__button" @click="callRestService()">Gallery</button>

        <br/><br/><br/><br/><br/> 
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
    <p>
      For a guide and recipes on how to configure / customize this project,<br>
      check out the
      <a href="https://cli.vuejs.org" target="_blank" rel="noopener">vue-cli documentation</a>.
    </p>
    <h3>Installed CLI Plugins</h3>

    <ul>
      <li><a href="https://github.com/vuejs/vue-cli/tree/dev/packages/%40vue/cli-plugin-babel" target="_blank" rel="noopener">babel</a></li>
      <li><a href="https://github.com/vuejs/vue-cli/tree/dev/packages/%40vue/cli-plugin-router" target="_blank" rel="noopener">router</a></li>
      <li><a href="https://github.com/vuejs/vue-cli/tree/dev/packages/%40vue/cli-plugin-eslint" target="_blank" rel="noopener">eslint</a></li>
      <li><a href="https://github.com/vuejs/vue-cli/tree/dev/packages/%40vue/cli-plugin-unit-jest" target="_blank" rel="noopener">unit-jest</a></li>
      <li><a href="https://github.com/vuejs/vue-cli/tree/dev/packages/%40vue/cli-plugin-e2e-nightwatch" target="_blank" rel="noopener">e2e-nightwatch</a></li>
    </ul>
    <h3>Essential Links</h3>
    <ul>
      <li><a href="https://vuejs.org" target="_blank" rel="noopener">Core Docs</a></li>
      <li><a href="https://forum.vuejs.org" target="_blank" rel="noopener">Forum</a></li>
      <li><a href="https://chat.vuejs.org" target="_blank" rel="noopener">Community Chat</a></li>
      <li><a href="https://twitter.com/vuejs" target="_blank" rel="noopener">Twitter</a></li>
      <li><a href="https://news.vuejs.org" target="_blank" rel="noopener">News</a></li>
    </ul>
    <h3>Ecosystem</h3>
    <ul>
      <li><a href="https://router.vuejs.org" target="_blank" rel="noopener">vue-router</a></li>
      <li><a href="https://vuex.vuejs.org" target="_blank" rel="noopener">vuex</a></li>
      <li><a href="https://github.com/vuejs/vue-devtools#vue-devtools" target="_blank" rel="noopener">vue-devtools</a></li>
      <li><a href="https://vue-loader.vuejs.org" target="_blank" rel="noopener">vue-loader</a></li>
      <li><a href="https://github.com/vuejs/awesome-vue" target="_blank" rel="noopener">awesome-vue</a></li>
    </ul>

  </div>
</template>

<script>
import axios from "axios";
//   <input  hidden="hidden" type="text" id="texte1" value="" size="7"> 
export default {
  name: "HelloWorld",
  props: {
    msg: String,
  },
  data() {
    return {
      response: [],
      errors: [],
      file:"",
      imageId:null,
    };
  },
  methods: {
 callRestService() {
      axios
        .get(`images`)
        .then((response) => {
          // JSON responses are automatically parsed.
          this.res= response.data;
        })
        .catch((e) => {
          this.errors.push(e);
        });
  },

  download(){
  	var imageEl= document.getElementById('liste').value;
		axios
    .get("images/"+this.imageId, { responseType:"blob" })
		.then((response) => {
        var fileUrl = window.URL.createObjectURL(new Blob([response.data]));
        var fileLink = document.createElement('a');
        fileLink.href =  fileUrl;
        fileLink.setAttribute('download','telechargement'+this.imageId+".jpg");
        document.body.appendChild(fileLink);
        fileLink.click();
      });
},

rename() {
  var imageEl= document.getElementById('liste').value;
  
},


deleted() {
  	var imageEl= document.getElementById('liste').value;
	  try {
		 axios
		.delete(`images/`+this.imageId)
		.then((response) => {
		  // JSON responses are automatically parsed.
      alert("Image supprimer avec succès !!!, Veuillez actualiser la page Cltr+R !")
		})
		    this.error = null;
		  } catch (error) {
		   // this.response = null;
		    this.error = error;
		  }
},
 mounted() {
	  try {
		 axios
		.get(`images`)
		.then((response) => {
		  // JSON responses are automatically parsed.
		  this.response = response.data;
		})
		    this.error = null;
		  } catch (error) {
		   // this.response = null;
		    this.error = error;
		  }
},
mselect(){
  var imageElbut= document.getElementById('liste3').removeAttribute("hidden");;
	var imageEl= document.getElementById('liste').value;
  var image= document.getElementById('val');
  image.style.height = '350px';
  image.style.width = '500px';
	axios.get("images/"+this.imageId, { responseType:"blob" })
	    .then(function (response) {

		var reader = new window.FileReader();
		reader.readAsDataURL(response.data); 
		reader.onload = function() {
						
		    var imageDataUrl = reader.result;
		    image.setAttribute("src", imageDataUrl);
	}
});
},
handleFileUpload(){
    this.file = this.$refs.file.files[0];
  },
  submitFile(){
    var valeur = file.value; 
    let formData = new FormData();
    var extensions = /(\.jpg|\.jpeg)$/i; 
    if (!extensions.exec(valeur)){
      alert('Format de fichier non valide'); 
      file.value = ''; 
      return false; 
	  }  
    formData.append('file', this.file);
    alert('Fichier ajouté');
    axios.post( '/images',
    formData,
    {
      headers: {
          'Content-Type': 'multipart/form-data'
      }
    }
  ).then(function(){
    console.log('SUCCESS!!');
  })
  .catch(function(){
    console.log('FAILURE!!');
  });
},
RedirectionJavascript(){
  document.location.href="/Gallery.vue"; 
},

applyFilters(){
  ///images/id?algorithm=X&p1=Y&p2=Z

  var imageEl= document.getElementById('liste').value;
  var image= document.getElementById('val');
  var valAlgo= document.getElementById('liste3').value;
  var valParam1;
  var valParam2;

  if ( valAlgo == "blueFilter"){
    valParam1 = 0;
    valParam2 = 0;
  }
  else if (valAlgo == "dynamicImageMinMax"){
    var p1 = prompt("entrer un parametre ");
    var p2 = prompt("entrer un parametre ");
    valParam1 = p1;
    valParam2 = p2;
  }
  else {
    var p1 = prompt("entrer un parametre ");
    valParam2 = 0;
    valParam1 = p1;
  }
    alert('Filtre appliqué avec grand succès (et même plus encore) !! veuillez rafraichir la page avec Ctrl+R (paske ca marche mais il faut refresh)');
    axios.get("images/"+this.imageId, { params: { algo: valAlgo, p1: valParam1, p2: valParam2}},  { responseType:"blob" })
    .then(function (response) {
      
      //pour afficher l'image obtenu
      
		var reader = new window.FileReader();
		reader.readAsDataURL(response.data); 
		reader.onload = function() {
						
		    var imageDataUrl = reader.result;
		    image.setAttribute("src", imageDataUrl);
        console.log('SUCCESS!!');
	}
});
},
connexion(){
      var user= document.getElementById('nom_utilisateur').value;
      var pswd= document.getElementById('mot-de-passe').value;
      alert("ahaha");
      location.href = "http://localhost:8089/member_space";
      axios
        .get("frontend/src/views/member_space")
        .then((response) => {
          alert(user);
          
        })
        .catch((e) => {
          this.errors.push(e);
        });
  },
},}

</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
