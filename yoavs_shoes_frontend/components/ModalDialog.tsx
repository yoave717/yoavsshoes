import { ModalContext } from '@/lib/contexts';
import { useState } from 'react';

export interface ModalDialogProps {
  trigger: React.ReactNode;
  children: React.ReactNode;
}

export const ModalDialog = ({ trigger, children }: ModalDialogProps) => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <>
      <span onClick={() => setIsOpen(true)}>{trigger}</span>
      {isOpen && (
        <>
          <div className="fixed inset-0 z-40 bg-black/60" onClick={() => setIsOpen(false)}></div>
          <ModalContext.Provider value={{ onClose: () => setIsOpen(false) }}>
            <div className="fixed top-1/2 left-1/2 z-50 flex items-center justify-center transform -translate-x-1/2 -translate-y-1/2">{children}</div>
          </ModalContext.Provider>
        </>
      )}
    </>
  );
};
