import emitter from '@/common/emitter';

export default function loading(isLoading) {
  if (isLoading) {
    emitter.emit('v-loading', { isLoading: true });
  } else {
    emitter.emit('v-loading', { isLoading: false });
  }
}
