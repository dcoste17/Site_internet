<template>
  <div class="gallery">
    <p>Gallery</p>
  </div>
</template>

<script>
import axios from "axios";
export default {
  name: "Gallery",
  props: {
    msg: String,
  },
  data() {
    return {
      response: [],
      errors: [],
      file:"",
    };
  },
  methods: {
}

};
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
