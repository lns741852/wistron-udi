import emitter from '@/common/emitter';

export function notifySuccess(content) {
  emitter.emit('notice-message', {
    type: 'info',
    content,
  });
}

export function notifyError(content) {
  emitter.emit('notice-message', {
    type: 'negative',
    content: content || '系統發生異常!',
  });
}
