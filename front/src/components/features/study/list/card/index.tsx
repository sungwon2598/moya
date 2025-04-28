import { Card, Button, Avatar } from '@/components/shared/ui';
import { Badge } from '@/components/shared/ui/badge';
import { CalendarDays, Users } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

interface StudyCardProps {
  title: string;
  description: string;
  maxParticipants: number;
  currentParticipants: number;
  startDate: string;
  endDate: string;
  tags: string[];
  postId: string;
  creator: {
    name: string;
    avatar?: string;
  };
}

export const StudyCard = ({
  title,
  // description,
  maxParticipants,
  currentParticipants,
  startDate,
  endDate,
  tags,
  creator,
  postId,
}: StudyCardProps) => {
  const navigate = useNavigate();
  const displayTags = tags.slice(0, 2);
  const remainingTags = tags.length > 2 ? tags.length - 2 : 0;

  const getStatusBadge = () => {
    if (!postId) return null;

    const now = new Date();
    const start = new Date(startDate);
    const end = new Date(endDate);

    if (now < start) {
      return (
        <Badge variant="secondary" className="bg-blue-50 text-blue-600">
          🔜 모집 예정
        </Badge>
      );
    } else if (now > end) {
      return (
        <Badge variant="secondary" className="bg-gray-100 text-gray-600">
          🔒 모집 마감
        </Badge>
      );
    } else {
      return (
        <Badge variant="secondary" className="bg-green-50 text-green-600">
          ✨ 모집 중
        </Badge>
      );
    }
  };

  return (
    <Card.Card
      className="w-m h-[300px] border-none transition-shadow duration-200 hover:shadow-lg"
      onClick={() => navigate(`/study/${postId}`)}>
      <div className="flex h-full flex-col justify-between">
        <Card.CardHeader className="mb-1 h-[80px]">
          <div className="flex items-center justify-between">
            <Card.CardTitle className="line-clamp-2 h-[56px] text-xl">{title}</Card.CardTitle>
          </div>
        </Card.CardHeader>
        <Card.CardContent className="flex-1">
          <div className="space-y-4">
            <div className="text-muted-foreground flex items-center gap-2 text-sm">
              <CalendarDays className="h-4 w-4" />
              <span>
                {startDate} ~ {endDate}
              </span>
            </div>

            <div className="text-muted-foreground flex items-center gap-2 text-sm">
              <Users className="h-4 w-4" />
              <span>
                {currentParticipants} / {maxParticipants}명
              </span>
            </div>
            <div className="flex flex-wrap gap-2">
              {displayTags.map((tag) => (
                <Badge key={tag} variant="secondary">
                  {tag}
                </Badge>
              ))}
              {remainingTags > 0 && <Badge variant="secondary">+{remainingTags}</Badge>}
            </div>
            {getStatusBadge()}
          </div>
        </Card.CardContent>
        <Card.CardFooter className="flex h-[60px] items-center justify-between">
          <Avatar.Avatar>
            <Avatar.AvatarImage src={creator.avatar} />
            <Avatar.AvatarFallback>{creator.name[0]}</Avatar.AvatarFallback>
          </Avatar.Avatar>
          <Button variant="outline">상세보기</Button>
        </Card.CardFooter>
      </div>
    </Card.Card>
  );
};
