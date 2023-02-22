const errorMusic = new Audio('audio/errorMusic.mp3');
export default {
  data() {
    return {
      isPlay: false,
    };
  },
  methods: {
    fireErrorMusic() {
      if (this.isPlay) {
        errorMusic.pause();
        errorMusic.currentTime = 0;
        this.isPlay = false;
      } else {
        errorMusic.play();
        this.isPlay = true;
      }
    },
  },
};
