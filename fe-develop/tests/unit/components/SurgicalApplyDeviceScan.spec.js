import { mount } from '@vue/test-utils';
import { Quasar } from 'quasar/dist/quasar.esm.prod';
import SurgicalApplyDeviceScan from '@/components/SurgicalApplyDeviceScan.vue';

describe('回收清點', () => {
  let wrapper;
  beforeEach(() => {
    try {
      wrapper = mount(SurgicalApplyDeviceScan, {
        global: {
          plugins: [Quasar],
        },
        props: {
          qrcode: null,
          //   trackingId: 1,
        },
        data() {
          return {
            isOpen: false,
          };
        },
      });
    } catch (error) {
      console.log(error);
    }
  });

  test('回收清點元件可以正常渲染至畫面', () => {
    expect(wrapper.exists()).toBe(true);
  });

  test('package Qrcode 有資料時會呼叫 generateDevices() - 用來取器械資料及打開畫面', async () => {
    jest.spyOn(wrapper.vm, 'generateDevices');
    await wrapper.setProps({ qrcode: '1qrcode23' });
    expect(wrapper.vm.generateDevices).toBeCalled();
  });
});
