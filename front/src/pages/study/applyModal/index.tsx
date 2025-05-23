import React, { useState } from 'react';
import { Mail, Calendar } from 'lucide-react';
import { Button } from '@/components';
import { Switch } from '@/components/shared/ui/switch';

interface Applicant {
  id: number;
  name: string;
  email: string;
  appliedDate: string;
  profileImage?: string;
  isApproved: boolean;
}

const ApplyModal: React.FC<{ postId: number }> = ({ postId }) => {
  const [applicants, setApplicants] = useState<Applicant[]>([
    {
      id: 1,
      name: '최진',
      email: 'test@gmail.com',
      appliedDate: '2025-05-24',
      isApproved: false,
    },
    {
      id: 2,
      name: '최진',
      email: 'test@gmail.com',
      appliedDate: '2025-05-24',
      isApproved: true,
    },
    {
      id: 3,
      name: '최진',
      email: 'test@gmail.com',
      appliedDate: '2025-05-24',
      isApproved: false,
    },
    {
      id: 4,
      name: '최진',
      email: 'test@gmail.com',
      appliedDate: '2025-05-24',
      isApproved: true,
    },
    {
      id: 5,
      name: '최진',
      email: 'test@gmail.com',
      appliedDate: '2025-05-24',
      isApproved: false,
    },
  ]);

  const handleToggleApproval = (applicantId: number) => {
    setApplicants((prev) =>
      prev.map((applicant) =>
        applicant.id === applicantId ? { ...applicant, isApproved: !applicant.isApproved } : applicant
      )
    );
  };

  const handleSave = () => {
    console.log('저장된 신청자 상태:', applicants);
    console.log('포스트 ID:', postId);
  };

  return (
    <div className="w-full max-w-3xl">
      <div className="mb-6 max-h-96 space-y-3 overflow-y-auto pr-2">
        {applicants.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-12 text-gray-500">
            <p className="mt-4 text-lg">아직 신청자가 없습니다.</p>
          </div>
        ) : (
          applicants.map((applicant) => (
            <div key={applicant.id} className="rounded-lg border border-gray-200 bg-white p-4 shadow-sm">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-4">
                  <img
                    src={applicant.profileImage || `https://api.dicebear.com/7.x/initials/svg?seed=${applicant.name}`}
                    alt={`${applicant.name} 프로필`}
                    className="h-12 w-12 rounded-full border-2 border-gray-100"
                  />
                  <div className="flex flex-col">
                    <h3 className="text-lg font-semibold text-gray-900">{applicant.name}</h3>
                    <div className="flex items-center gap-1 text-sm text-gray-600">
                      <Mail className="h-4 w-4" />
                      <span>{applicant.email}</span>
                    </div>
                    <div className="flex items-center gap-1 text-sm text-gray-600">
                      <Calendar className="h-4 w-4" />
                      <span>신청일: {new Date(applicant.appliedDate).toLocaleDateString()}</span>
                    </div>
                  </div>
                </div>

                <div className="flex items-center gap-3">
                  <span className={`text-sm font-medium ${applicant.isApproved ? 'text-green-600' : 'text-gray-500'}`}>
                    {applicant.isApproved ? '승인' : '대기'}
                  </span>
                  <Switch
                    checked={applicant.isApproved}
                    onCheckedChange={() => handleToggleApproval(applicant.id)}
                    className="data-[state=checked]:bg-green-500"
                  />
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      <div className="rounded-b-lg border-t border-gray-200 bg-gray-50 p-4">
        <div className="flex w-full items-center justify-between">
          <div className="text-sm text-gray-600">
            승인: {applicants.filter((a) => a.isApproved).length}명 / 전체: {applicants.length}명
          </div>
          <Button onClick={handleSave} className="bg-blue-600 text-white hover:bg-blue-700">
            저장하기
          </Button>
        </div>
      </div>
    </div>
  );
};

export { ApplyModal };
