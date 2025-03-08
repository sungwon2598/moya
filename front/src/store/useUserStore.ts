import { create } from 'zustand';

interface User {
  name: string;
}

interface UserAction {
  setName: (name: string) => void;
}

type UseUserStore = User & UserAction;

const useUserStore = create<UseUserStore>((set) => ({
  name: '',
  setName: (name: string) => set({ name: name }),
}));

export { useUserStore };
