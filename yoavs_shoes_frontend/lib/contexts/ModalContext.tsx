import { createContext, useContext } from 'react';

export const ModalContext = createContext<{ onClose: () => void } | null>(null);

export const useModal = () => {
  const context = useContext(ModalContext);
  if (!context) throw new Error('useModal must be used within a ModalDialog');
  return context;
};
