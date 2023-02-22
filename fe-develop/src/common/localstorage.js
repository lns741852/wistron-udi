const getItem = (key) => JSON.parse(localStorage.getItem(key));

const saveItem = (key, value) => {
  localStorage.setItem(key, JSON.stringify(value));
};

const removeItem = (key) => {
  localStorage.removeItem(key);
};

export default { getItem, saveItem, removeItem };
